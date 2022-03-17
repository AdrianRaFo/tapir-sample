import cats.Parallel
import cats.effect._
import cats.implicits._
import fs2.Stream
import io.circe.Json
import io.circe.syntax.KeyOps
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.{Logger => ServerLogMiddleware, _}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.model.StatusCode.InternalServerError
import sttp.monad.MonadError
import sttp.tapir.integ.cats.CatsMonadError
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.ValuedEndpointOutput
import sttp.tapir.server.interceptor.exception.{ExceptionContext, ExceptionHandler}
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.statusCode

import scala.concurrent.duration._

object Server {

  def serve[F[_]](implicit F: Async[F], P: Parallel[F]): Stream[F, ExitCode] = {
    implicit val monadError: MonadError[F] = new CatsMonadError[F]

    Stream.eval(Slf4jLogger.create[F]).flatMap { logger =>
      implicit val http4sServerInterpreter: Http4sServerInterpreter[F] = {
        val options = Http4sServerOptions
          .customInterceptors[F, F]
          .exceptionHandler(serverErrorHandler)
          .serverLog(
            DefaultServerLog[F](
              doLogWhenHandled = (message, t) => t.fold(logger.debug(message))(logger.debug(_)(message)),
              doLogAllDecodeFailures = (message, t) => t.fold(logger.error(message))(logger.error(_)(message)),
              doLogExceptions = (_, t) => logger.error(t)(Option(t.getMessage).orEmpty),
              logWhenHandled = false,
              logAllDecodeFailures = false,
              logLogicExceptions = true
            )
          )
          .options

        Http4sServerInterpreter(options)
      }

      val routes = Routes.tapir[F]
      BlazeServerBuilder[F]
        .bindHttp()
        .withHttpApp(routes.orNotFound)
        .serve
    }
  }

  val serverErrorHandler: ExceptionHandler = (_: ExceptionContext) =>
    ValuedEndpointOutput(
      statusCode(InternalServerError).and(jsonBody[Json]),
      Json.obj("message" := "An internal error occurred, check logs for more information")
    ).some

}

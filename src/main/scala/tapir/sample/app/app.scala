package tapir.sample

import cats.effect.{Async, Sync}
import cats.syntax.all._
import io.circe.Json
import io.circe.syntax.KeyOps
import org.typelevel.log4cats.StructuredLogger
import sttp.model.StatusCode.InternalServerError
import sttp.model.{Header, StatusCode}
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler._
import sttp.tapir.server.interceptor.decodefailure.{DecodeFailureHandler, DefaultDecodeFailureHandler}
import sttp.tapir.server.interceptor.exception.{ExceptionContext, ExceptionHandler}
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.interceptor.{DecodeFailureContext, ValuedEndpointOutput}
import sttp.tapir.{DecodeResult, EndpointIO, EndpointInput, statusCode}

package object app {

  private def failingInput(ctx: DecodeFailureContext) = {
    import sttp.tapir.internal.RichEndpointInput
    ctx.failure match {
      case DecodeResult.Missing =>
        def missingAuth(i: EndpointInput[_]) =
          i.pathTo(ctx.failingInput).collectFirst { case a: EndpointInput.Auth[_, _] => a }

        missingAuth(ctx.endpoint.securityInput)
          .orElse(missingAuth(ctx.endpoint.input))
          .getOrElse(ctx.failingInput)
      case _                    => ctx.failingInput
    }
  }

  private def customRespond(ctx: DecodeFailureContext): Option[(StatusCode, List[Header])] =
    failingInput(ctx) match {
      case _: EndpointIO.Body[_, _] => Some((StatusCode.UnprocessableEntity, Nil))
      case _                        => respond(ctx, false, false)
    }

  def http4sServerInterpreter[F[_]: Async](logger: StructuredLogger[F]): Http4sServerInterpreter[F] = {
    val serverErrorHandler: ExceptionHandler = (_: ExceptionContext) =>
      ValuedEndpointOutput(
        statusCode(InternalServerError).and(jsonBody[Json]),
        Json.obj("message" := "An internal error occurred, check logs for more information")
      ).some

    val decodeFailureHandler: DecodeFailureHandler = DefaultDecodeFailureHandler(
      customRespond,
      FailureMessages.failureMessage,
      failureResponse
    )

    val options = Http4sServerOptions
      .customInterceptors[F, F]
      .exceptionHandler(serverErrorHandler)
      .decodeFailureHandler(decodeFailureHandler)
      .serverLog(
        DefaultServerLog[F](
          doLogWhenHandled = (message, t) => t.fold(logger.debug(message))(logger.debug(_)(message)),
          doLogAllDecodeFailures = (message, t) => t.fold(logger.error(message))(logger.error(_)(message)),
          doLogExceptions = (_, t) => logger.error(t)(Option(t.getMessage).orEmpty),
          noLog = Sync[F].unit,
          logWhenHandled = false,
          logAllDecodeFailures = false,
          logLogicExceptions = true
        )
      )
      .options

    Http4sServerInterpreter(options)
  }

}

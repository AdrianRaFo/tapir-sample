package tapir.sample.app

import cats.effect.{Async, ExitCode}
import cats.syntax.all._
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tapir.sample.api.Routes

object Server {

  def serve[F[_]: Async] = {

    Slf4jLogger.create[F].flatMap { logger =>
      implicit val interpreter: Http4sServerInterpreter[F] = http4sServerInterpreter(logger)

      val routes = Routes.tapir[F]

      EmberServerBuilder
        .default[F]
        .withHost(host"0.0.0.0")
        .withHttpApp(routes.orNotFound)
        .build
        .use(_ => Async[F].never[ExitCode])
    }
  }

}

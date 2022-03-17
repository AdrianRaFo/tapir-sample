import cats.effect.Concurrent
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Routes {

  def tapir[F[_]: Concurrent](implicit interpreter: Http4sServerInterpreter[F]): HttpRoutes[F] = {
    val helloRoute =
      Endpoints.helloEndpoint.serverLogicRecoverErrors {
        case name if name.trim.nonEmpty => HelloResponse(s"Hello $name").pure[F]
        case name => Concurrent[F].raiseError[HelloResponse](BadRequestResponse(name, "Received name was empty"))
      }

    interpreter.toRoutes(List(helloRoute))
  }

}

import cats.effect.Concurrent
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Routes {

  def tapir[F[_]: Concurrent](implicit interpreter: Http4sServerInterpreter[F]): HttpRoutes[F] = {
    val helloRoute =
      Endpoints.helloEndpoint.serverLogicRecoverErrors {
        case name if name.forall(_.isLetter) => HelloResponse(s"Hello $name").pure[F]
        case name =>
          Concurrent[F].raiseError[HelloResponse](BadRequestResponse(name, "Names can only contain letters"))
      }

    interpreter.toRoutes(List(helloRoute))
  }

}

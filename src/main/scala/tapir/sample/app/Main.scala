package tapir.sample.app

import cats.effect._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = Server.serve[IO]
}

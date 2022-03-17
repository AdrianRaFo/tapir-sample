import sttp.model.StatusCode._
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

object Endpoints {

  val helloEndpoint = endpoint.get
    .name("hello")
    .description("Return hello message")
    .in(
      "hello" / path[String]("name")
    )
    .out(
      jsonBody[HelloResponse]
        .example(HelloResponse.example)
        .description("Hello message for name")
    )
    .errorOut(
      oneOf[Throwable](
        oneOfVariant(
          BadRequest,
          jsonBody[BadRequestResponse].description("Invalid received request")
        )
      )
    )

  val all = List(helloEndpoint).map(_.tag("hello-v1"))

}

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.EndpointIO.Example
import sttp.tapir.Schema

case class HelloResponse(helloMessage: String)

object HelloResponse {
  implicit val codec: Codec[HelloResponse] = deriveCodec

  implicit val schema: Schema[HelloResponse] = Schema
    .derived[HelloResponse]
    .description("Response with a successful hello message")
    .modify(_.helloMessage)(_.description("The hello message"))

  val example = Example.of(
    HelloResponse("Hello Foo")
  )
}

case class BadRequestResponse(name: String, error: String) extends Throwable

object BadRequestResponse {
  implicit val codec: Codec[BadRequestResponse] = deriveCodec

  implicit val schema: Schema[BadRequestResponse] = Schema
    .derived[BadRequestResponse]
    .description("Bad request response for hello call")
    .modify(_.name)(_.description("The received name"))
    .modify(_.error)(_.description("The failure reason"))

}

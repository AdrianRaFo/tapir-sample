package tapir.sample.api

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.EndpointIO.Example
import sttp.tapir.Schema

case class HelloResponse(helloMessage: String)
object HelloResponse {
  implicit val codec: Codec[HelloResponse] = deriveUnwrappedCodec

  implicit val schema: Schema[HelloResponse] = Schema
    .string[HelloResponse]
    .description("Successful hello message response")

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

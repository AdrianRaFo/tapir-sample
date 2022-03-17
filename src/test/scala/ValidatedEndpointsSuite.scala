import munit.{TapirGoldenOpenAPISuite, TapirGoldenOpenAPIValidatorSuite}
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsOptions
import sttp.tapir.openapi.Info

import java.nio.file.Path

class ValidatedEndpointsSuite extends TapirGoldenOpenAPISuite with TapirGoldenOpenAPIValidatorSuite {

  override val endpoints: List[AnyEndpoint] = Endpoints.all

  override def tapirGoldenOpenAPIInfo: Info = Info("Sample", "latest")
  //create file at project root level
  override def tapirGoldenOpenAPIPath: Path = super.tapirGoldenOpenAPIPath.getParent.getParent.getParent

  override def tapirGoldenOpenAPIOptions: OpenAPIDocsOptions =
    OpenAPIDocsOptions.default.copy(defaultDecodeFailureOutput = _ => None)
}

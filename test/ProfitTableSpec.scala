import models.ProfitTable
import org.scalatest.{ FlatSpec, MustMatchers }
import play.api.libs.json.Json

class ProfitTableSpec extends FlatSpec with MustMatchers with DataFixtures {

  import models.JsonProtocol._

  "Json string" should "be proper serialized to ProfitTable" in {

    val profitTable = Json.parse(jsonCreateProfitTableRequest).validate[ProfitTable].get
    profitTable.bankId mustBe "someBank"
    profitTable.values mustBe Vector(Vector(1.0, 2.3), Vector(2.7, 5.6))

  }
}

import models.{CreditColumn, Profit, ShareRow}
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class ApplicationSpec extends PlaySpec with OneAppPerTest with DataFixtures {

  "Routes" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
    }

  }

  "ProfitController" should {

    "create profit table" in {

      status(
        route(app,
              FakeRequest(POST, "/profit/table/someBank")
                .withJsonBody(Json.parse(jsonCreateProfitTableRequest))).get
      ) mustBe 200

      val resp = contentAsString(
        route(app, FakeRequest(GET, "/profit/table/someBank")).get
      )
      resp mustBe Json.parse(jsonCreateProfitTableRequest).toString()
    }

    "calculate profit" in {
      status(
        route(app,
              FakeRequest(POST, "/profit/table/someBank")
                .withJsonBody(Json.parse(jsonCreateProfitTableRequest))).get
      ) mustBe 200

      val req = route(app,
                      FakeRequest(POST, "/profit/calculate/someBank")
                        .withJsonBody(Json.parse(jsonCalculateProfitRequest))).get

      status(req) mustBe 200
      contentAsString(req) mustBe "2.3"
    }

    "calculate profit with fixed profit (read from configuration)" in {

      val req = route(app,
                      FakeRequest(POST, "/profit/calculate/someBank")
                        .withJsonBody(Json.parse(jsonCalculateProfitRequest2))).get

      status(req) mustBe 200
      contentAsString(req) mustBe "1.1"
    }

    "calculate profit after edited cell" in {
      status(
        route(app,
              FakeRequest(POST, "/profit/table/someBank")
                .withJsonBody(Json.parse(jsonCreateProfitTableRequest))).get
      ) mustBe 200

      val req = route(app,
                      FakeRequest(POST, "/profit/calculate/someBank")
                        .withJsonBody(Json.parse(jsonCalculateProfitRequest))).get

      status(req) mustBe 200
      contentAsString(req) mustBe "2.3"

      import models.JsonProtocol._
      val profit = Profit(ShareRow(10, 40), CreditColumn(40000, 70000), "someBank", 4.3)
      status(
        route(app,
              FakeRequest(POST, "/profit/edit/cell")
                .withJsonBody(Json.toJson(profit))).get
      ) mustBe 200

      val req2 = route(app,
                       FakeRequest(POST, "/profit/calculate/someBank")
                         .withJsonBody(Json.parse(jsonCalculateProfitRequest))).get

      status(req2) mustBe 200
      contentAsString(req2) mustBe "4.3"
    }
  }

}

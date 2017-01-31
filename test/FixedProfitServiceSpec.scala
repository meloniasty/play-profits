import models.FixedProfit
import org.scalatestplus.play._
import services.FixedProfitService

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class FixedProfitServiceSpec extends PlaySpec with OneAppPerSuite {

  "Fixed profit service component" should {

    "resolve implementation and fetch results" in {
      val fixedProfitService: FixedProfitService = app.injector.instanceOf[FixedProfitService]

      Await
        .result(fixedProfitService.fetchProfit("someBank"), Duration.Inf)
        .get mustBe FixedProfit(
        12,
        1.1
      )

      Await.result(fixedProfitService.fetchProfit("unknown"), Duration.Inf) mustBe None
    }
  }
}

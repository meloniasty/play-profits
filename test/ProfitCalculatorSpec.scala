import models._
import org.scalatest.{ FlatSpec, MustMatchers }
import services.{ FixedProfitService, ProfitCalculator, ProfitTableRepository }

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration

class ProfitCalculatorSpec extends FlatSpec with MustMatchers {
  import scala.concurrent.ExecutionContext.Implicits.global

  trait UnimplementedMethods {
    def remove(bankId: String): Future[Unit] = ???

    def update(profit: Profit): Future[Unit] = ???

    def save(profitTable: ProfitTable): Future[Unit] = ???

    def get(bankId: String): Future[Option[ProfitTable]] = ???
  }

  "Profit calculator with empty values" should "proper calculate profit for given request" in {
    val table = new ProfitTableRepository with UnimplementedMethods {
      override def listProfits(bankId: String): Future[List[Profit]] = Future {
        List()
      }
    }

    val fixedProfitService = new FixedProfitService {
      override def fetchProfit(bankId: String): Future[Option[FixedProfit]] = Future {
        None
      }
    }

    val calculator = new ProfitCalculator(table, fixedProfitService)

    val calculate = ProfitCalculate(3, 30000, 10)

    val res = Await.result(calculator.calculate("bankId", calculate), Duration.Inf)

    res mustBe None
  }

  "Profit calculator with empty fixed profit" should "proper calculate profit for given request" in {

    val table = new ProfitTableRepository with UnimplementedMethods {
      override def listProfits(bankId: String): Future[List[Profit]] = Future {
        List(
          Profit(ShareRow(10, 20), CreditColumn(0, 10000), "bankId", 1.0),
          Profit(ShareRow(10, 20), CreditColumn(10000, 30000), "bankId", 2.0),
          Profit(ShareRow(20, 40), CreditColumn(0, 10000), "bankId", 1.5),
          Profit(ShareRow(20, 40), CreditColumn(10000, 30000), "bankId", 2.5)
        )
      }
    }

    val fixedProfitService = new FixedProfitService {
      override def fetchProfit(bankId: String): Future[Option[FixedProfit]] = Future {
        None
      }
    }

    val calculator = new ProfitCalculator(table, fixedProfitService)

    val calculate = ProfitCalculate(3, 25000, 30)

    val res = Await.result(calculator.calculate("bankId", calculate), Duration.Inf)

    res mustBe Some(2.5)
  }

  "Profit calculator with fixed profit" should "proper calculate profit for given request" in {

    val table = new ProfitTableRepository with UnimplementedMethods {
      override def listProfits(bankId: String): Future[List[Profit]] = Future {
        List(
          Profit(ShareRow(10, 20), CreditColumn(0, 10000), "bankId", 1.0),
          Profit(ShareRow(10, 20), CreditColumn(10000, 30000), "bankId", 2.0),
          Profit(ShareRow(20, 40), CreditColumn(0, 10000), "bankId", 1.5),
          Profit(ShareRow(20, 40), CreditColumn(10000, 30000), "bankId", 2.5)
        )
      }
    }

    val fixedProfitService = new FixedProfitService {
      override def fetchProfit(bankId: String): Future[Option[FixedProfit]] = Future {
        Some(FixedProfit(10, 3.0))
      }
    }

    val calculator = new ProfitCalculator(table, fixedProfitService)

    val calculate = ProfitCalculate(3, 30000, 30)

    val res = Await.result(calculator.calculate("bankId", calculate), Duration.Inf)

    res mustBe Some(3.0)
  }
}

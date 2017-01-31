package services

import javax.inject.Singleton

import com.google.inject.Inject
import models.{ Profit, ProfitCalculate }

import scala.concurrent.{ ExecutionContext, Future }

trait ProfitCalculatorService {
  def calculate(bankId: String, profitCalculate: ProfitCalculate): Future[Option[Double]]
}

@Singleton
class ProfitCalculator @Inject()(
    val profitTableRepository: ProfitTableRepository,
    val fixedProfitService: FixedProfitService
)(implicit ec: ExecutionContext)
    extends ProfitCalculatorService {

  override def calculate(bankId: String,
                         profitCalculate: ProfitCalculate): Future[Option[Double]] =
    fixedProfitService.fetchProfit(bankId).flatMap {
      case Some(fp) if fp.noOfMonths >= profitCalculate.currentMonth =>
        Future.successful(Some(fp.value))
      case _ => calculateFromRepository(bankId, profitCalculate)
    }

  private def calculateFromRepository(bankId: String,
                                      profitCalculate: ProfitCalculate): Future[Option[Double]] =
    profitTableRepository
      .listProfits(bankId)
      .map(list => {
        list.find(isProfitInBoundaries(profitCalculate)).map(_.value)
      })

  private def isProfitInBoundaries(profitCalculate: ProfitCalculate): Profit => Boolean = {
    profit =>
      {
        val basicCheck = profit.share.min <= profitCalculate.ownShare &&
          profit.share.max > profitCalculate.ownShare &&
          profit.credit.min <= profitCalculate.creditAmount

        if (profit.credit.max.isDefined) {
          basicCheck && profit.credit.max.get > profitCalculate.creditAmount
        } else {
          basicCheck
        }

      }
  }
}

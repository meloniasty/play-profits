package services

import com.google.inject.Inject
import models.FixedProfit
import play.api.Configuration

import scala.concurrent.{ ExecutionContext, Future }

trait FixedProfitService {
  def fetchProfit(bankId: String): Future[Option[FixedProfit]]
}

class ConfigurationFixedProfitService @Inject()(
    val configuration: Configuration
)(implicit ec: ExecutionContext)
    extends FixedProfitService {
  private val configBanks = configuration.getConfig("profits.fixedProfit")
  override def fetchProfit(bankId: String): Future[Option[FixedProfit]] = Future {
    configBanks.flatMap(conf => {
      for {
        noOfMonths <- conf.getInt(s"$bankId.noOfMonths")
        value      <- conf.getDouble(s"$bankId.value")
      } yield FixedProfit(noOfMonths, value)
    })
  }
}

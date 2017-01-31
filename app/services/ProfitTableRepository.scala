package services

import java.util.concurrent.{ ConcurrentHashMap, ConcurrentMap }
import javax.inject.{ Inject, Singleton }

import models.{ Profit, ProfitTable }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait ProfitTableRepository {

  def listProfits(bankId: String): Future[List[Profit]]

  def remove(bankId: String): Future[Unit]

  def update(profit: Profit): Future[Unit]

  def save(profitTable: ProfitTable): Future[Unit]

  def get(bankId: String): Future[Option[ProfitTable]]
}

@Singleton
class InMemoryProfitTableRepository @Inject()(implicit ec: ExecutionContext)
    extends ProfitTableRepository {

  private val map: ConcurrentMap[String, List[Profit]] =
    new ConcurrentHashMap[String, List[Profit]]()

  override def listProfits(bankId: String): Future[List[Profit]] = Future {
    map.getOrDefault(bankId, List.empty)
  }

  override def remove(bankId: String): Future[Unit] = Future {
    map.remove(bankId)
  }

  override def update(profit: Profit): Future[Unit] = Future {

    val list = map.get(profit.bankId)

    val updatedList = list.map {
      case p if p.credit == profit.credit && p.share == profit.share => profit
      case p                                                         => p
    }
    map.put(profit.bankId, updatedList)

  }

  override def save(profitTable: ProfitTable): Future[Unit] = Future {
    map.remove(profitTable.bankId)
    map.put(profitTable.bankId, profitTable.toProfits.toList)
  }

  override def get(bankId: String): Future[Option[ProfitTable]] = Future {
    Try {
      ProfitTable(map.get(bankId))
    }.toOption
  }
}

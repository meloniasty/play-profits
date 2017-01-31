package models

case class ProfitTable(bankId: String,
                       columns: Vector[CreditColumn],
                       rows: Vector[ShareRow],
                       values: Vector[Vector[Double]]) {
  require(rows.length == values.length)
  require(columns.length == values(0).length)

  def toProfits: Vector[Profit] =
    columns.indices
      .flatMap(i => {
        rows.indices
          .map(j => {
            Profit(rows(j), columns(i), bankId, values(j)(i))
          })
          .toVector
      })
      .toVector
}

case class CreditColumn(min: Int, max: Option[Int] = None) {
  if (max.isDefined) {
    require(min < max.get)
  }
}

case class ShareRow(min: Int, max: Int) {
  require(min < max)
}

case class Profit(share: ShareRow, credit: CreditColumn, bankId: String, value: Double)

object ProfitTable {

  def apply(list: List[Profit]): ProfitTable = {
    require(list.nonEmpty)

    val bankId = list.head.bankId

    val rows    = list.map(_.share).distinct.sortBy(_.min).toVector
    val columns = list.map(_.credit).distinct.sortBy(_.min).toVector
    val values = {
      rows.map(share => {
        columns.map(credit => {
          list.find(profit => profit.credit == credit && profit.share == share).map(_.value).get
        })
      })
    }
    new ProfitTable(bankId, columns, rows, values)
  }
}

object CreditColumn {
  def apply(min: Int, max: Int): CreditColumn = new CreditColumn(min, Some(max))
}

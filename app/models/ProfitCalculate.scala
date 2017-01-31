package models

case class ProfitCalculate(currentMonth: Int, creditAmount: Int, ownShare: Int) {
  require(currentMonth > 0)
  require(ownShare <= 100)
}

package models

import play.api.libs.json.Json

object JsonProtocol {

  implicit val profitRowJsonFormat    = Json.format[ShareRow]
  implicit val profitColumnJsonFormat = Json.format[CreditColumn]
  implicit val profitTableJsonFormat  = Json.format[ProfitTable]

  implicit val profitCalculateJsonFormat = Json.format[ProfitCalculate]

  implicit val profitJsonFormat = Json.format[Profit]

}

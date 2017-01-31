package controllers

import javax.inject._

import models.{ Profit, ProfitCalculate, ProfitTable }
import play.api.data.validation.ValidationError
import play.api.libs.json.{ JsPath, Json }
import play.api.mvc.{ Action, Controller, Result }
import services.{ ProfitCalculatorService, ProfitTableRepository }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ProfitController @Inject()(
    val profitCalculatorService: ProfitCalculatorService,
    val profitTableRepository: ProfitTableRepository
)(implicit exec: ExecutionContext)
    extends Controller {

  import models.JsonProtocol._

  def calculate(bankId: String) = Action.async(parse.tolerantJson) { implicit request =>
    request.body
      .validate[ProfitCalculate]
      .fold(
        err => errorToJson(err),
        pc =>
          profitCalculatorService.calculate(bankId, pc).map {
            case Some(profit) => Ok(Json.toJson(profit))
            case _            => NotFound
        }
      )
  }

  def fetchTable(bankId: String) = Action.async { implicit request =>
    profitTableRepository.get(bankId).map {
      case Some(pt) => Ok(Json.toJson(pt))
      case None     => NotFound
    }
  }

  def saveTable(bankId: String) = Action.async(parse.tolerantJson) { implicit request =>
    request.body
      .validate[ProfitTable]
      .fold(
        err => errorToJson(err),
        pt => profitTableRepository.save(pt.copy(bankId = bankId)).map(_ => Ok)
      )

  }

  def editCell() = Action.async(parse.tolerantJson) { implicit request =>
    request.body
      .validate[Profit]
      .fold(
        err => errorToJson(err),
        profit => profitTableRepository.update(profit).map(_ => Ok)
      )

  }

  /**
    * Simple print error in json way
    */
  private def errorToJson(err: Seq[(JsPath, Seq[ValidationError])]): Future[Result] =
    Future.successful(BadRequest(err.map(_._2.mkString(";")).mkString("\n")))
}

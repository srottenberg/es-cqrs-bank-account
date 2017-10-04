package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import java.util.UUID

import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}

import command.{AccountCommandService, CreateAccountCommand, DepositCommand}
import query.AccountQueryService

class AccountController(
  val controllerComponents: ControllerComponents,
  accountCommandService: AccountCommandService,
  accountQueryService: AccountQueryService
) extends BaseController {

  val create = Action.async {
    val generatedId = UUID.randomUUID().toString

    val command = CreateAccountCommand(generatedId)

    accountCommandService.sendCommand(generatedId, command).map {
      case Right(_) => Ok(generatedId)
      case Left(error) => BadRequest(error.message)
    }
  }

  def get(id: String) = Action.async {
    accountQueryService.findById(id).map {
      case Some(account) => Ok(Json.toJson(account))
      case None => NotFound
    }
  }

  val list = Action.async {
    accountQueryService.findAll().map { accounts =>
      Ok(Json.toJson(accounts))
    }
  }

  def deposit(id: String, amount: Double) = Action.async {
    val command = DepositCommand(amount)
    accountCommandService.sendCommand(id, command).map {
      case Right(_) => Ok
      case Left(error) => BadRequest(error.message)
    }
  }
}

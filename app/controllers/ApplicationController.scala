package controllers

import play.api.mvc.{BaseController, ControllerComponents}

class ApplicationController(val controllerComponents: ControllerComponents) extends BaseController {

  val index = Action { Ok("Hi !") }

}

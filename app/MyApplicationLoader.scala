import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{BroadcastHub, Keep, Source}
import command.{AccountCommandService, AccountEvent}
import play.filters.HttpFiltersComponents
import query.AccountQueryService
import router.Routes

class MyApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    new MyComponents(context).application
  }
}

class MyComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents {

  val (eventQueue, eventSource) = Source.queue(100, OverflowStrategy.fail)
    .toMat(BroadcastHub.sink[AccountEvent])(Keep.both)
    .run()(materializer)

  val accountCommandService = new AccountCommandService(actorSystem, eventQueue)
  val accountQueryService = new AccountQueryService(actorSystem, eventSource, materializer)

  lazy val router = new Routes(
    httpErrorHandler,
    new controllers.ApplicationController(controllerComponents),
    new controllers.AccountController(controllerComponents, accountCommandService, accountQueryService)
  )

}

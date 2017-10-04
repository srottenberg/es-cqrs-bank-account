package command

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.scaladsl.SourceQueueWithComplete
import akka.util.Timeout
import command.AccountWriteActor.SendCommand

class AccountCommandService(
  actorSystem: ActorSystem,
  eventQueue: SourceQueueWithComplete[AccountEvent]
) {

  val writeActor = actorSystem.actorOf(AccountWriteActor.props(eventQueue))

  implicit val timeout = Timeout(5 seconds)

  def sendCommand(id: String, command: AccountCommand): Future[Either[AccountError, Unit]] = {
    (writeActor ? SendCommand(id, command)).mapTo[Either[AccountError, Unit]]
  }

}

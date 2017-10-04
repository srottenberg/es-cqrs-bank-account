package command

import scala.collection.mutable

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.stream.scaladsl.SourceQueueWithComplete
import domain.{AccountState, EmptyAccountState}

object AccountWriteActor {
  def props(queue: SourceQueueWithComplete[AccountEvent]) = Props(new AccountWriteActor(queue))

  case class SendCommand(accountId: String, command: AccountCommand)
}

class AccountWriteActor(queue: SourceQueueWithComplete[AccountEvent]) extends Actor {
  import AccountWriteActor._

  val eventStore = mutable.Map[String, Seq[AccountEvent]]()

  def receive = {
    case SendCommand(accountId, command) =>
      val currentAccount = getAccount(accountId)
      AccountHandlers.handle(currentAccount, command) match {
        case Right(_) =>
          val newEvent = AccountEvent(
            id = UUID.randomUUID().toString,
            accountId = accountId,
            command = command
          )
          val events = eventStore.getOrElse(accountId, Seq())
          eventStore.put(accountId, events :+ newEvent)
          queue.offer(newEvent)
          sender() ! Right(())
        case Left(error) =>
          sender() ! error
      }

  }

  private def getAccount(accountId: String): AccountState = {
    eventStore.get(accountId).map { events =>
      events.foldLeft[AccountState](EmptyAccountState) {
        case (account, event) => AccountHandlers.handle(account, event.command).right.get // safe because the eventStore has been validated
      }
    }.getOrElse(EmptyAccountState)
  }
}

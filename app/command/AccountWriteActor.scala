package command

import scala.collection.mutable

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
      AccountHandlers.handleCommand(currentAccount, command) match {
        case Right(event) =>
          val events = eventStore.getOrElse(accountId, Seq())
          eventStore.put(accountId, events :+ event)
          queue.offer(event)
          sender() ! Right(())
        case Left(error) =>
          sender() ! Left(error)
      }

  }

  private def getAccount(accountId: String): AccountState = {
    eventStore.get(accountId).map { events =>
      events.foldLeft[AccountState](EmptyAccountState) {
        case (account, event) => AccountHandlers.applyEvent(account, event)
      }
    }.getOrElse(EmptyAccountState)
  }
}

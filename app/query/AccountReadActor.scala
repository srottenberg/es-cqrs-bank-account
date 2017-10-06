package query

import scala.collection.mutable

import akka.actor.{Actor, Props}
import command.{AccountEvent, AccountHandlers}
import domain.{AccountState, EmptyAccountState}

object AccountReadActor {
  def props = Props[AccountReadActor]

  case class Get(id: String)
  case object GetAll
}

class AccountReadActor extends Actor {
  import AccountReadActor._

  val accountStore = mutable.Map[String, AccountState]()

  def receive = {
    case event: AccountEvent =>
      val currentAccount = accountStore.getOrElse(event.accountId, EmptyAccountState)
      val updatedAccount = AccountHandlers.applyEvent(currentAccount, event)
      accountStore.put(event.accountId, updatedAccount)
    case Get(accountId) =>
      sender() ! accountStore.get(accountId)
    case GetAll =>
      sender() ! accountStore.values.toList
  }
}

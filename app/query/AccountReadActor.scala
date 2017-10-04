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
    case AccountEvent(_, accountId, command) =>
      val currentAccount = accountStore.getOrElse(accountId, EmptyAccountState)
      val updatedAccount = AccountHandlers.handle(currentAccount, command).right.get // safe because events are validated
      accountStore.put(accountId, updatedAccount)
    case Get(accountId) =>
      sender() ! accountStore.get(accountId)
    case GetAll =>
      sender() ! accountStore.values.toList
  }
}

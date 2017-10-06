package command

import java.util.UUID

import domain.{AccountState, EmptyAccountState, OpenAccountState}

object AccountHandlers {

  def handleCommand(currentState: AccountState, command: AccountCommand): Either[AccountError, AccountEvent] =
    (currentState, command) match {
      case (EmptyAccountState, CreateAccountCommand(id)) =>
        Right(
          AccountCreatedEvent(UUID.randomUUID().toString, id, command)
        )
      case (OpenAccountState(id, _), DepositCommand(amount)) =>
        Right(
          AmountAddedEvent(UUID.randomUUID().toString, id, amount, command)
        )
      case _ =>
        Left(AccountError(s"Unhandled command $command for state ${currentState.getClass.getSimpleName}"))
    }

  def applyEvent(currentState: AccountState, event: AccountEvent): AccountState =
    (currentState, event) match {
      case (EmptyAccountState, AccountCreatedEvent(_, idAccount, _)) =>
        OpenAccountState(idAccount, 0)
      case (account: OpenAccountState, AmountAddedEvent(_, _, amount, _)) =>
        val newBalance  = account.balance + amount
        account.copy(balance = newBalance)
      case _ => throw new IllegalStateException(s"Could not apply event ${event.id}")
    }

}

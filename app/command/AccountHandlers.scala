package command

import java.util.UUID

import domain.{AccountState, ClosedAccountState, EmptyAccountState, OpenAccountState}

object AccountHandlers {

  def handleCommand(currentState: AccountState, command: AccountCommand): Either[AccountError, AccountEvent] =
    (currentState, command) match {
      case (EmptyAccountState, CreateAccountCommand(id)) =>
        Right(
          AccountCreatedEvent(UUID.randomUUID().toString, id, command)
        )
      case (OpenAccountState(id, _), DepositCommand(amount)) if amount > 0 =>
        Right(
          AmountAddedEvent(UUID.randomUUID().toString, id, amount, command)
        )
      case (OpenAccountState(id, balance), WithdrawalCommand(amount)) if amount > 0 =>
        if (balance - amount >= 0) {
          Right(
            AmountAddedEvent(UUID.randomUUID().toString, id, amount * -1, command)
          )
        } else {
          Left(
            AccountError(s"Withdrawal not authorized, not enough money!")
          )
        }
      case (OpenAccountState(id, balance), CloseAccountCommand) =>
        if (balance == 0) {
          Right(
            AccountClosedEvent(UUID.randomUUID().toString, id, command)
          )
        } else {
          Left(
            AccountError("Could not close not empty account!")
          )
        }
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
      case (account: OpenAccountState, _: AccountClosedEvent) =>
        ClosedAccountState(account.id)
      case _ => throw new IllegalStateException(s"Could not apply event ${event.id}")
    }

}

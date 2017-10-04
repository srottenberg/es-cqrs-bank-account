package command

import domain.{AccountState, EmptyAccountState, OpenAccountState}

object AccountHandlers {

  def handle(currentState: AccountState, command: AccountCommand): Either[AccountError, AccountState] =
    (currentState, command) match {
      case (EmptyAccountState, CreateAccountCommand(id)) =>
        Right(OpenAccountState(id, 0d))
      case (OpenAccountState(id, balance), DepositCommand(amount)) =>
        Right(OpenAccountState(id, balance + amount))
      case _ =>
        Left(AccountError(s"Unhandled command $command for state ${currentState.getClass.getSimpleName}"))
    }

}

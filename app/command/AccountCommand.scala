package command

sealed trait AccountCommand

case class CreateAccountCommand(id: String) extends AccountCommand
case class DepositCommand(amount: Double) extends AccountCommand
case class WithdrawalCommand(amount: Double) extends AccountCommand
case object CloseAccountCommand extends AccountCommand

package command

sealed trait AccountCommand

case class CreateAccountCommand(id: String) extends AccountCommand
case class DepositCommand(amount: Double) extends AccountCommand
case class WithdrawCommand(amount: Double) extends AccountCommand
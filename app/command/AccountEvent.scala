package command

sealed trait AccountEvent {
  def id: String

  def accountId: String

  def command: AccountCommand
}

case class AccountCreatedEvent(id: String, accountId: String, command: AccountCommand) extends AccountEvent
case class AmountAddedEvent(id: String, accountId: String, amount: Double, command: AccountCommand) extends AccountEvent
case class AccountClosedEvent(id: String, accountId: String, command: AccountCommand) extends AccountEvent

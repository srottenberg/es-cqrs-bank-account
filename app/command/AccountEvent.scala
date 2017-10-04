package command

case class AccountEvent(
  id: String,
  accountId: String,
  command: AccountCommand
)

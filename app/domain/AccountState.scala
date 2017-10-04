package domain

import play.api.libs.json.OFormat

import julienrf.json.derived

sealed trait AccountState

case object EmptyAccountState extends AccountState

sealed trait NonEmptyAccountState extends AccountState {
  def id: String
}
object NonEmptyAccountState {
  implicit val format: OFormat[NonEmptyAccountState] = derived.oformat()
}

case class OpenAccountState(id: String, balance: Double) extends NonEmptyAccountState

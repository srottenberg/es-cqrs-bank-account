package query

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.util.Timeout
import command.AccountEvent
import domain.NonEmptyAccountState
import query.AccountReadActor.{Get, GetAll}

class AccountQueryService(
  actorSystem: ActorSystem,
  eventSource: Source[AccountEvent, NotUsed],
  materializer: Materializer
) {

  val readActor = actorSystem.actorOf(AccountReadActor.props)

  implicit val timeout = Timeout(5 seconds)

  eventSource
    .toMat(Sink.foreach { event => readActor ! event })(Keep.right)
    .run()(materializer)


  def findById(id: String): Future[Option[NonEmptyAccountState]] = {
    (readActor ? Get(id)).mapTo[Option[NonEmptyAccountState]]
  }

  def findAll(): Future[Seq[NonEmptyAccountState]] = {
    (readActor ? GetAll).mapTo[Seq[NonEmptyAccountState]]
  }

}

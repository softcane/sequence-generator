package com.softcane.sequence.service

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.pattern.after
import com.softcane.sequence.dal.SequenceRepo

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

class SequenceGeneratorException(msg: String) extends RuntimeException(msg)

@Singleton
class SequenceGenerator @Inject()(repo: SequenceRepo)(implicit system: ActorSystem, ec: ExecutionContext) {
  import SequenceGenerator._
  private[this] lazy val table = repo.sequence

  def getAndInc(name: String): Future[Long] = {
    def retry(remaining: Int): Future[Long] = {
      table.get(name).flatMap {
        case None => Future.failed(new SequenceGeneratorException(s"Sequence '$name' not found.") with NoStackTrace)
        case Some(v) => table.incrementIf(name, v.sequence).flatMap {
          case res if res.wasApplied() => Future.successful(v.sequence)
          case _ if remaining > 0 => after(delay, system.scheduler)(retry(remaining - 1))
        }
      }
    }
    retry(retryTimes)
  }
}

object SequenceGenerator {
  val retryTimes = 3
  val delay = 300.milliseconds
}
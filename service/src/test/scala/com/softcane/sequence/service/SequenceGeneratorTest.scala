package com.softcane.sequence.service

import akka.actor.ActorSystem
import com.outworkers.phantom.dsl._
import com.softcane.sequence.dal.SequenceRepo
import com.outworkers.phantom.connectors.ContactPoint
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Future


object Connector {
  val testConn = ContactPoint.embedded.noHeartbeat().keySpace("test_keyspace")
}

trait EmbeddedDatabase  {
  val repo = new SequenceRepo(Connector.testConn)
}

class SequenceGeneratorTest extends FlatSpec with Suite with Matchers with ScalaFutures with BeforeAndAfterAll with EmbeddedDatabase {

  implicit val defaultPatience = PatienceConfig(Span(120, Seconds), Span(1000, Millis))

  override def beforeAll() = {
    super.beforeAll()
    repo.create()
  }

  override def afterAll() = {
    super.afterAll()
    repo.shutdown()
  }

  trait ServiceBuilder {
    implicit lazy val actorSystem = ActorSystem("sequence-generator-test")
    val name = "testService"
    val startValue = 100L

    val service = new SequenceGenerator(repo)
    repo.truncate()
  }

  private def drop(name: String): Future[ResultSet] = {
    for {
      r <- repo.sequence.deleteSeq(name)
    } yield r
  }

  it should "increment sequence by one" in new ServiceBuilder {
    whenReady(repo.sequence.store(name, startValue)) { insert =>
      whenReady(service.getAndInc(name)) { generate =>
        whenReady(repo.sequence.get(name)) {
          case Some(v) =>
            val nextSeq = startValue + 1
            v.sequence should be(nextSeq)
            drop(name)
          case None =>
            drop(name)
        }
      }
    }
  }
}

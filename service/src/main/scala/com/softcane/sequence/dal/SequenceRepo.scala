package com.softcane.sequence.dal

import com.outworkers.phantom.dsl._
import javax.inject.{Inject, Singleton}

import com.outworkers.phantom.connectors.CassandraConnection

import scala.concurrent.Future

@Singleton
class SequenceRepo @Inject() (override val connector: CassandraConnection) extends Database[SequenceRepo](connector) {

  object sequence extends SequenceModel with Connector

  abstract class SequenceModel extends Table[SequenceModel, Sequence] {

    override def tableName: String = "sequence_gen"

    object name extends StringColumn with PartitionKey
    object sequence extends LongColumn

    def store(name: String, sequence: Long): Future[ResultSet] = {
      insert
        .value(_.name, name)
        .value(_.sequence, sequence)
        .consistencyLevel_=(ConsistencyLevel.QUORUM)
        .future()
    }
    def incrementIf(name: String, prevSeq: Long): Future[ResultSet] = {
      update
        .where(_.name eqs name)
        .modify(_.sequence setTo (1 + prevSeq))
        .onlyIf(_.sequence is prevSeq)
        .consistencyLevel_=(ConsistencyLevel.QUORUM)
        .future()
    }
    def get(name: String): Future[Option[Sequence]] = {
      select
        .where(_.name eqs name)
        .consistencyLevel_=(ConsistencyLevel.QUORUM)
        .one()
    }
    def deleteSeq(name: String): Future[ResultSet] = {
      delete
        .where(_.name eqs name)
        .consistencyLevel_=(ConsistencyLevel.ONE)
        .future()
    }
  }
}
case class Sequence(name: String, sequence: Long)
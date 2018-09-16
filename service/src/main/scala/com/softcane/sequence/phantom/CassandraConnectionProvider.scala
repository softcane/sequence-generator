package com.softcane.sequence.phantom

import javax.inject.{Inject, Provider, Singleton}

import com.outworkers.phantom.connectors.ContactPoint.DefaultPorts
import com.typesafe.config.ConfigFactory
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import play.api.Configuration

import scala.collection.JavaConverters._

@Singleton
class CassandraConnectionProvider @Inject()(configuration: Configuration) extends Provider[CassandraConnection] {
  lazy val get = {
    val config = ConfigFactory.load()
    val hosts = config.getStringList("cassandra.host")
    val keyspace: String = config.getString("cassandra.keyspace")

    ContactPoints(hosts.asScala, DefaultPorts.live).keySpace(keyspace)
  }
}
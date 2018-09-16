package com.softcane.sequence.phantom

import javax.inject.Provider

import com.typesafe.config.ConfigFactory
import com.outworkers.phantom.connectors.{ContactPoints, CassandraConnection}
import play.api.Configuration

import scala.collection.JavaConverters._

class CassandraConnectionProvider(configuration: Configuration) extends Provider[CassandraConnection] {
  lazy val get = {
    val config = ConfigFactory.load()
    val hosts = config.getStringList("cassandra.host")
    val keyspace: String = config.getString("cassandra.keyspace")

    ContactPoints(hosts.asScala).keySpace(keyspace)
  }
}
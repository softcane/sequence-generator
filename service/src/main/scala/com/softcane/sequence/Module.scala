package com.softcane.sequence

import com.google.inject.AbstractModule
import com.softcane.sequence.phantom.CassandraConnectionProvider
import com.outworkers.phantom.connectors.{ContactPoint, CassandraConnection}
import play.api.{Configuration, Environment, Mode}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  
  def configure() {
    if(environment.mode == Mode.Test)
      bind(classOf[CassandraConnection]).toProvider(classOf[CassandraConnectionProvider])
    else
      bind(classOf[CassandraConnection]).toInstance(ContactPoint.embedded.noHeartbeat().keySpace("test_keyspace"))
  }
}
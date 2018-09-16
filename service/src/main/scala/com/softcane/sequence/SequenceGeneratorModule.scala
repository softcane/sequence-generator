package com.softcane.sequence

import com.softcane.sequence.phantom.CassandraConnectionProvider
import com.outworkers.phantom.connectors.CassandraConnection
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

class SequenceGeneratorModule extends Module {
  def bindings(environment: Environment, config: Configuration): Seq[Binding[_]] = {
    Seq(bind[CassandraConnection].toProvider(classOf[CassandraConnectionProvider]))
  }
}
package com.softcane.sequence.controllers

import javax.inject.{Inject, Singleton}

import com.softcane.model.Sequence
import com.softcane.sequence.service.SequenceGenerator
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SequenceController @Inject() (sequence: SequenceGenerator) extends InjectedController  {

  def generate() = Action.async { _ =>
    sequence.getAndInc("id").map { seq =>
      Ok(Json.toJson(Sequence(seq, System.currentTimeMillis())))
    }.recover {
      case th => InternalServerError(Json.obj("error" -> th.toString))
    }
  }
}

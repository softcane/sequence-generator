package com.softcane.model

import play.api.libs.json.Json

case class Sequence(id: Long, createdAt: Long)

object Sequence {
  implicit val JsonFormat = Json.format[Sequence]
}
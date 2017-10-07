package models


import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import play.api.libs.json.{JsString, JsValue, Json, Writes}

case class Ticket (
                    id: Int,
                    url: Option[String],
                    ticketType: Option[String],
                    subject: Option[String],
                    description: Option[String],
                    priority: Option[String],
                    status: Option[String],
                    tags: Option[Seq[String]],
                    created_at: Option[ZonedDateTime],
                    updated_at: Option[ZonedDateTime]
                  )

object Ticket {
  implicit val dateWriter = new Writes[ZonedDateTime] {
    def writes(zonedDateTime: ZonedDateTime): JsValue = {
      JsString(zonedDateTime.format(DateTimeFormatter.ISO_INSTANT))
    }
  }
  implicit val ticketReads = Json.reads[Ticket]
  implicit val ticketWrites = Json.writes[Ticket]
}
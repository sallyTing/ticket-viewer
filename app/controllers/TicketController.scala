package controllers

import java.time.ZonedDateTime
import javax.inject._

import models.Ticket
import play.api.Configuration
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.Future


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TicketController @Inject()(cc: ControllerComponents,ws: WSClient, config: Configuration) extends AbstractController(cc) {

  val ticketURL = config.get[String]("zendesk.accountSite") + "/api/v2/tickets.json"
  val singleTicketURL = config.get[String]("zendesk.accountSite") + "/api/v2/tickets/"
  val user = config.get[String]("zendesk.user")
  val password = config.get[String]("zendesk.password")


  def getAllTicket(page: String, jsonType: String) = Action.async { implicit request: Request[AnyContent] =>
    val url = if (page != "") {
      ticketURL + "?page=" + page
    } else {
      ticketURL
    }
    val request: WSRequest = ws.url(url).addHttpHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis).withAuth(user, password, WSAuthScheme.BASIC)
    request.get().map { response =>
      if (response.status == 200) {
        Json.parse(response.body) match {
          case jsResult: JsObject =>
            val count = (jsResult \ "count").as[Int]
            val previousPage = (jsResult \ "previous_page").asOpt[String].map(_.dropWhile(c => c != '?'))
            val nextPage = (jsResult \ "next_page").asOpt[String].map(_.dropWhile(c => c != '?'))
            val tickets = (jsResult \ "tickets").as[JsArray].value.map{ rawTicket =>
              parseJsonToTicket(rawTicket)
            }
            if (jsonType.toLowerCase() == "true") Ok(Json.obj("tickets" -> tickets, "count" -> count, "previous" -> previousPage, "next" -> nextPage))
            else Ok(views.html.tickets(tickets, count, previousPage, nextPage))
          case _ =>
            val message = "Error in response. Cannot parse response to json."
            if (jsonType.toLowerCase() == "true") InternalServerError(Json.obj("message" -> message))
            else InternalServerError(views.html.errorDisplay(message))
        }
      } else {
        val message = "Cannot retrieve tickets information from Source: " + url + ", Status: " + response.status
        if (jsonType.toLowerCase() == "true") InternalServerError(Json.obj("message" -> message))
        else InternalServerError(views.html.errorDisplay(message))
      }
    }
  }

  def getOneTicket(id: String, jsonType: String) = Action.async { implicit request: Request[AnyContent] =>
    if (id != "") {
      val url = singleTicketURL + id + ".json"
      val request: WSRequest = ws.url(url).addHttpHeaders("Accept" -> "application/json")
        .withRequestTimeout(10000.millis).withAuth(user, password, WSAuthScheme.BASIC)
      request.get().map { response =>
        if (response.status == 200) {
          Json.parse(response.body) match {
            case jsResult: JsObject =>
              val rawTicket = (jsResult \ "ticket").as[JsObject]
              val ticket = parseJsonToTicket(rawTicket)
              if (jsonType.toLowerCase() == "true") Ok(Json.obj("ticket" -> ticket))
              else Ok(views.html.ticket(ticket))
          }
        } else {
          val message = "Cannot retrieve ticket information from Source: " + url + ", Status: " + response.status
          if (jsonType.toLowerCase() == "true") InternalServerError(Json.obj("message" -> message))
          else InternalServerError(views.html.errorDisplay(message))
        }
      }
    } else {
      val message = "No id provided"
      if (jsonType.toLowerCase() == "true") Future.successful(BadRequest(Json.obj("message" -> message)))
      else Future.successful(BadRequest(views.html.errorDisplay(message)))
    }

  }

  def parseJsonToTicket(rawTicket: JsValue): Ticket = Ticket(
    id = (rawTicket \ "id").as[Int],
    url = (rawTicket \ "url").asOpt[String],
    ticketType = (rawTicket \ "type").asOpt[String],
    subject = (rawTicket \ "subject").asOpt[String],
    description = (rawTicket \ "description").asOpt[String],
    priority = (rawTicket \ "priority").asOpt[String],
    status = (rawTicket \ "status").asOpt[String],
    tags = (rawTicket \ "tags").asOpt[Seq[String]],
    created_at = (rawTicket \ "created_at").asOpt[ZonedDateTime],
    updated_at = (rawTicket \ "updated_at").asOpt[ZonedDateTime]
  )
}

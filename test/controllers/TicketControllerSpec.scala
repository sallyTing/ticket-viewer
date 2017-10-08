package controllers

import com.google.gson.JsonObject
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{JsArray, JsObject}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, _}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class TicketControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "TicketController GET" should {

    "render the tickets page from the application" in {
      val controller = inject[TicketController]
      val ticketsPage = controller.getAllTicket("", "").apply(FakeRequest(GET, "/tickets"))

      status(ticketsPage) mustBe OK
      contentType(ticketsPage) mustBe Some("text/html")
      contentAsString(ticketsPage) must include ("tickets in total")
    }

    "render the tickets page from the router" in {
      val ticketsPage = route(app, FakeRequest(GET, "/tickets")).get

      status(ticketsPage) mustBe OK
      contentType(ticketsPage) mustBe Some("text/html")
      contentAsString(ticketsPage) must include ("tickets in total")
    }

    "render single ticket page from the router" in {
      val ticketPage = route(app, FakeRequest(GET, "/tickets/1")).get

      status(ticketPage) mustBe OK
      contentType(ticketPage) mustBe Some("text/html")
      contentAsString(ticketPage) must include ("Ticket #1")
    }

    "render error page if input invalid id" in {
      val ticketPage = route(app, FakeRequest(GET, "/tickets/0")).get

      status(ticketPage) mustBe INTERNAL_SERVER_ERROR
      contentType(ticketPage) mustBe Some("text/html")
      contentAsString(ticketPage) must include ("Opps, Error")
    }

    "return the json from the application" in {
      val controller = inject[TicketController]
      val ticketsResult = controller.getAllTicket("", "true").apply(FakeRequest(GET, "/tickets?jsonType=true"))

      status(ticketsResult) mustBe OK
      contentType(ticketsResult) mustBe Some("application/json")
      val result = contentAsJson(ticketsResult).as[JsObject]
      (result \ "count").as[Int] must be > 0
      (result \ "tickets").as[JsArray].value.length must be > 0
    }

    "return the json from the router" in {
      val ticketsResult = route(app, FakeRequest(GET, "/tickets?jsonType=true")).get

      status(ticketsResult) mustBe OK
      contentType(ticketsResult) mustBe Some("application/json")
      val result = contentAsJson(ticketsResult).as[JsObject]
      (result \ "count").as[Int] must be > 0
      (result \ "tickets").as[JsArray].value.length must be > 0
    }
  }
}

# ticket-viewer

### Environment Setup

install sbt
```
$ bower install sbt
```
in "conf/application.conf", insert the test account detail
```
zendesk {
  accountSite = "[account site]"
  user = "[user account]"
  password = "[password]"
}
```
### run
Through command line, 'cd' to the folder, then type
```
sbt run
```

Ticket API
```
GET     /tickets                    controllers.TicketController.getAllTicket(page ?= "", jsonType ?="")
GET     /ticket/:id                 controllers.TicketController.getOneTicket(id, jsonType ?="")
```
In browser, directly go to "http://localhost:9000"

Click "here", it will call "http://localhost:9000/tickets"

If add parameter "jsonType=true" with this api "http://localhost:9000/tickets?jsonType=true", it will return json type response

This api also accept another parameter "page" to retrieve tickets on other page (if ticket total number is more than 100)

Each ticket subject is a link to "http://locahost:9000/ticket/[id]", from which can view each ticket.

It also accept parameter "jsonType=true"

### test
```
sbt test
```
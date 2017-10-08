# ticket-viewer

### Environment Setup

install sbt
```
$ bower install sbt
```
in "conf/application.conf", insert the test account detail
```
zendesk {
  user = "[useraccount]"
  password = "[password]"
}
```
### run
Through command line, 'cd' to the folder, then type
```
sbt run
```
In browser, directly go to "http://localhost:9000"

Click "here", it will call "http://localhost:9000/tickets"

If add parameter "jsonType=true" with this api "http://localhost:9000/tickets?jsonType=true", it will return json type response

This api also accept another parameter "page" to retrieve tickets on other page (if ticket total number is more than 100)
### test
```
sbt test
```
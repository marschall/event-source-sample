Event Source Example
====================
A small example on how to use [EventSource](http://dev.w3.org/html5/eventsource/) aka Server-Sent Events with Java.

You need [jetty-eventsource-servlet](https://github.com/jetty-project/jetty-eventsource-servlet) (released to Maven Central) then go to http://127.0.0.1:8080/event-source-sample/ and you see the server time that gets updated all ten seconds. Not very useful but shows you how to use the API.

Tomcat
------
To make the example work on Tomcat you need Tomcat 7 and have a NIO connector.

```xml
<Connector protocol="org.apache.coyote.http11.Http11NioProtocol" />
```

Check out the [the HTTP Connector reference](http://tomcat.apache.org/tomcat-7.0-doc/config/http.html) for more information.

You will get [chunking](http://tomcat.10.n6.nabble.com/How-to-disable-chunked-encoding-for-the-Http11NioProtocol-connector-td2038448.html) but it works anyways.

Resin
-----
Does chunking but works out of the box.

GlassFish
---------
Needs a not yet released version or a manual patch, see [GRIZZLY-1252](http://java.net/jira/browse/GRIZZLY-1252)

Jetty
-----
Works out of the box, even on Jetty 7.


Event Source Example
====================
A small example on how to use [EventSource](http://dev.w3.org/html5/eventsource/) aka Server-Sent Events with Java/Jetty.

You need [jetty-eventsource-servlet](https://github.com/jetty-project/jetty-eventsource-servlet) (not yet released, you need to build a SNAPSHOT) then go to http://127.0.0.1:8080/event-source-sample/ and you see the server time that gets updated all ten seconds. Not very useful but shows you how to use the API.

Tomcat
------
To make the example work on Tomcat you need Tomcat 7 and have a NIO connector.

    <Connector protocol="org.apache.coyote.http11.Http11NioProtocol" />

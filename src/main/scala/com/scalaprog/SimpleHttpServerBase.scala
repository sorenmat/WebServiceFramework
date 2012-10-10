package com.scalaprog

import com.sun.net.httpserver.{HttpExchange, HttpServer, HttpHandler}
import java.net.InetSocketAddress
import io.Source

abstract class SimpleHttpServerBase(val socketAddress: String = "127.0.0.1",
                                    val port: Int = 8080,
                                    val backlog: Int = 0) extends HttpHandler {
  private val address = new InetSocketAddress(socketAddress, port)
  private val server = HttpServer.create(address, backlog)
  server.createContext("/", this)

  def respond(exchange: HttpExchange, code: Int = 200, body: String = "") {
    val bytes = body.getBytes
    exchange.setAttribute("Content-Type", "text/xml")
    exchange.getResponseHeaders.add("Content-Type", "text/xml")
    exchange.sendResponseHeaders(code, bytes.size)
    exchange.getResponseBody.write(bytes)
    exchange.getResponseBody.write("\r\n\r\n".getBytes)
    exchange.getResponseBody.close()
    exchange.close()
  }

  def respondToPost(exchange: HttpExchange, code: Int = 200, wsClass: AnyRef) {
    val bytes = XmlGenerator.getResponse(Source.fromInputStream(exchange.getRequestBody).mkString, wsClass).toString().getBytes
    exchange.setAttribute("Content-Type", "text/xml")
    exchange.getResponseHeaders.add("Content-Type", "text/xml")
    exchange.sendResponseHeaders(code, bytes.size)
    exchange.getResponseBody.write(bytes)
    //exchange.getResponseBody.write("\r\n\r\n".getBytes)
    exchange.getResponseBody.close()
    exchange.close()
  }

  def start() = server.start()

  def stop(delay: Int = 1) = server.stop(delay)
}

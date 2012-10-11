package com.scalaprog

import collection.immutable.HashMap
import com.sun.net.httpserver.HttpExchange

abstract class SimpleHttpServer(override val socketAddress: String = "127.0.0.1",
                                override val port: Int = 8080,
                                override val backlog: Int = 0) extends SimpleHttpServerBase(socketAddress, port, backlog) {
  private var getMappings = new HashMap[String, () => Any]
  private var postMappings = new HashMap[String, AnyRef]

  def get(path: String)(action: => Any) = getMappings += path -> (() => action)

  def post(path: String, wsClass: AnyRef) = {
    postMappings += path -> wsClass
  }

  def handle(exchange: HttpExchange) = {
    println(exchange.getRequestMethod)
    exchange.getRequestMethod match {
      case "GET" => handleGet(exchange)
      case "POST" => handlePost(exchange)
      case _ => respond(exchange, 500, "Unknown method call")

    }
  }

  def handlePost(exchange: HttpExchange) = {
    println("handlePost: " + exchange.getRequestURI.getPath)
    println(postMappings.mkString(", "))
    val serviceName = exchange.getRequestURI.getPath
    postMappings.get(serviceName) match {
      case None => {
        println("Error unable to serve "+serviceName)
        respond(exchange, 404)
      }
      case Some(action) => try {
        respondToPost(exchange, 200, action)
      } catch {
        case ex: Exception => respond(exchange, 500, ex.toString)
      }
    }
  }

  def handleGet(exchange: HttpExchange) = {
    getMappings.get(exchange.getRequestURI.getPath) match {
      case None => respond(exchange, 404)
      case Some(action) => try {
        respond(exchange, 200, action().toString)
      } catch {
        case ex: Exception => respond(exchange, 500, ex.toString)
      }
    }
  }
}

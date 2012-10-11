package com.scalaprog

/**
 * User: soren
 */
object HttpServerMain {

  def main(args: Array[String]) {
    val server = new WebServiceServer("127.0.0.1", "http://localhost", "8888")
    server.register("Hello", new MyHelloWorld)
    server.register("Time", new ShowDate)
    server.register("PrintLine", new PrintLine)
    server.start()
  }
}

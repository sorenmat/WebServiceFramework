package com.scalaprog

/**
 * User: soren
 */
object HttpServerMain {

  def main(args: Array[String]) {
    val server = new HelloApp()
    server.register("Hello", new MyHelloWorld)
    server.register("Time", new ShowDate)
    server.register("PrintLine", new PrintLine)
    server.start()
  }
}

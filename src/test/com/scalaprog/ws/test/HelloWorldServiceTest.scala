package com.scalaprog.ws.test

import org.scalatest.FunSuite
import dispatch._
import com.scalaprog.{WebServiceServer, MyHelloWorld}


class HelloWorldServiceTest extends FunSuite {

  test("In Hello world webservice wsdl should contain MyHelloWorldServiceSoapBinding") {
    val server = new WebServiceServer("127.0.0.1", "http://localhost", "9090")
    server.register("Hello", new MyHelloWorld)
    server.start()

    val svc = url("http://localhost:9090/Hello")
    val wsdl = Http(svc OK as.String)().toString
    assert(wsdl contains ("MyHelloWorldServiceSoapBinding"))
    server.stop()
  }
}

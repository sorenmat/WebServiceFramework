package com.scalaprog.ws.test

import org.scalatest.FunSuite
import dispatch._
import com.scalaprog.{ListTestService, WebServiceServer, MyHelloWorld}


class ListTestServiceTest extends FunSuite {

  test("In Hello world webservice wsdl should contain MyHelloWorldServiceSoapBinding") {
    val server = new WebServiceServer("127.0.0.1", "http://localhost", "9090")
    server.register("ListService", new ListTestService)
    server.start()

    val svc = url("http://localhost:9090/ListService")
    val wsdl = Http(svc OK as.String)().toString
    println(wsdl)
    assert(wsdl contains ("ListTestServiceServiceSoapBinding"))
    assert(wsdl contains ("""<xs:element type="xs:list" name="return" minOccurs="0"></xs:element>"""))
    server.stop()
  }
}

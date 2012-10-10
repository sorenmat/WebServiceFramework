package com.scalaprog
import xml.{NodeSeq, Elem, XML, Unparsed}
import java.lang.reflect.Method


class ChildSelectable(ns: NodeSeq) {
  def \* = ns flatMap {
    _ match {
      case e: Elem => e.child
      case _ => NodeSeq.Empty
    }
  }
}


object XmlGenerator {

  implicit def nodeSeqIsChildSelectable(xml: NodeSeq) = new ChildSelectable(xml)

  /**
   * Generate the response to the client
   *
   * @param s
   * @param wsClass The web service class with the methods we expose
   * @return The xml response
   */
  def getResponse(s: String, wsClass: AnyRef) = {
    val reqXML = XML.loadString(s)
    val method = (reqXML \\ "Body" \*)
    val methodName = method.tail.head.label // this seems very hacky !!

    val returnValue = wsClass.getClass.getMethod(methodName).invoke(wsClass)
    val resp = <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        {if (returnValue != null)
        generateReturnValues(methodName, returnValue.toString)}
      </soap:Body>
    </soap:Envelope>
    resp
  }

  /**
   * Helper method for generating xml not supported by scala
   * @param methodName
   * @param returnValue
   * @return
   */
  def generateReturnValues(methodName: String, returnValue: String) = {
    Unparsed("<ns2:" + methodName + "Response xmlns:ns2=\"http://ws.unitlinked.schantz.com/\"> <return>" + returnValue + "</return> </ns2:getMessageResponse>             ")

  }

  def generateSchema(m: Method) = {
    val methodName = m.getName
    val returnType = m.getReturnType.getSimpleName
    val methodResponse = methodName + "Response"
    <xs:schema xmlns:tns="http://ws.unitlinked.schantz.com/" xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="unqualified" targetNamespace="http://ws.unitlinked.schantz.com/" version="1.0">
      <xs:element name={methodName} type={"tns:" + methodName}/>
      <xs:element name={methodResponse} type={"tns:" + methodResponse}/>
      <xs:complexType name={methodName}>
        <xs:sequence/>
      </xs:complexType>
      <xs:complexType name={methodResponse}>
        {if (!m.getReturnType.equals(Void.TYPE))
        <xs:sequence>
          <xs:element minOccurs="0" name="return" type={"xs:" + returnType.toLowerCase}/>
        </xs:sequence>}

      </xs:complexType>
    </xs:schema>
  }

  def getOperation(m: Method) = {
    val methodName = m.getName
    val returnType = m.getReturnType.getSimpleName
    val methodResponse = methodName + "Response"

    <wsdl:operation name={methodName}>
      <soap:operation soapAction=" " style="document"/>
      <wsdl:input name={methodName}>
        <soap:body use="literal"/>
      </wsdl:input>
      <wsdl:output name={methodResponse}>
        <soap:body use="literal"/>
      </wsdl:output>
    </wsdl:operation>
  }

  /**
   * Generate the WSDL
   * @param service
   * @param url
   * @return
   */
  def generateWSDL(service: AnyRef, url: String) = {
    val serviceName = service.getClass.getSimpleName
    val test = <wsdl:definitions xmlns:ns1="http://schemas.xmlsoap.org/soap/http" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tns="http://ws.unitlinked.schantz.com/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="HelloWorldService" targetNamespace="http://ws.unitlinked.schantz.com/">
      <wsdl:types>
        {service.getClass.getDeclaredMethods.map(m => {
        generateSchema(m)
      }
      )}
      </wsdl:types>{service.getClass.getDeclaredMethods.map(m => {

        <wsdl:message name={m.getName}>
          <wsdl:part element={"tns:" + m.getName} name="parameters"></wsdl:part>
        </wsdl:message>
          <wsdl:message name={m.getName + "Response"}>
            <wsdl:part element={"tns:" + m.getName + "Response"} name="parameters"></wsdl:part>
          </wsdl:message>
      }
      )}<wsdl:portType name={serviceName}>
        {service.getClass.getDeclaredMethods.map(m => {
          <wsdl:operation name={m.getName}>
            <wsdl:input message={"tns:" + m.getName} name={m.getName}></wsdl:input>
            <wsdl:output message={"tns:" + m.getName + "Response"} name={m.getName + "Response"}></wsdl:output>
          </wsdl:operation>
        })}
      </wsdl:portType>

      <wsdl:binding name={serviceName + "ServiceSoapBinding"} type={"tns:" + serviceName}>
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>{service.getClass.getDeclaredMethods.map(m => {
        getOperation(m)
      })}
      </wsdl:binding>

      <wsdl:service name={serviceName + "Service"}>
        <wsdl:port binding={"tns:" + serviceName + "ServiceSoapBinding"} name={serviceName + "Port"}>
          <soap:address location={url}/>
        </wsdl:port>
      </wsdl:service>

    </wsdl:definitions>

    Unparsed("<?xml version='1.0' encoding='UTF-8'?>") ++ test
  }


  def main(args: Array[String]) = {
    generateWSDL(new MyHelloWorld(), "http://url")
  }
}
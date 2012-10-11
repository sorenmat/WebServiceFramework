package com.scalaprog


class WebServiceServer(socketAddr: String, baseURL: String, port: String) extends SimpleHttpServer(socketAddr, port.toInt, 0) {

  def register(url: String, wsInstance: AnyRef) {
    get("/"+url) {
      XmlGenerator.generateWSDL(wsInstance, baseURL+":"+port+"/"+url)
    }
    post("/"+url, wsInstance)
  }
}

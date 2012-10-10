package com.scalaprog


class HelloApp extends SimpleHttpServer {

  def register(url: String, wsInstance: AnyRef) {
    get("/"+url) {
      XmlGenerator.generateWSDL(wsInstance, "http://localhost:8080/"+url)
    }
    post("/"+url, wsInstance)
  }
}

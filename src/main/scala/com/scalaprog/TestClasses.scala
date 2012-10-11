package com.scalaprog

import java.util.Date

class MyHelloWorld {
  def getMessage = "hejsa"
  def getLongMessage = "hejsa med dig, old friend"
}

class ListTestService {
  def getMessages = List("Hello", "World")
}

class ShowDate {
  def getDate = new Date().toString
}

class PrintLine {
  def print {
    println("I've been called")
  }

}

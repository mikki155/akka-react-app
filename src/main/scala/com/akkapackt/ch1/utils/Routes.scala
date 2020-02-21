package com.akkapackt.ch1.utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

class MyJsonService extends Directives with JsonSupport {
  val routes: Route = {
    path ("person") {
      get {
        complete((StatusCodes.OK, "Insert JSON data here"))
      } ~
        post {
          entity(as[Person]) { person =>
            println(person.firstName)
            println(person.lastName)
            println(person.phoneNumber)
            println(person.address)
            complete(StatusCodes.OK)
          }
        }
    }
  }
}

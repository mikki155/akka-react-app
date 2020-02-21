package com.akkapackt.ch1.utils

import akka.NotUsed
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, path, post, _}
import akka.http.scaladsl.server.Route
import spray.json._
import com.akkapackt.ch1.utils.MyJsonService

final case class Person(firstName: String, lastName: String, phoneNumber: String, address: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val personJsonFormat = jsonFormat4(Person)
}

object Main {
  def main(args: Array[String]): Unit = {
    ActorSystem(start, "sentimenttuning-rest")
  }

  def start: Behavior[NotUsed] = Behaviors.setup { ctx =>

    implicit val classicSystem = ctx.system.toClassic
    implicit val executionContext = ctx.executionContext

//    implicit val jsonObject = new JsonWriter[Person] {
//      def write(person: Person): JsValue = {
//        JsObject(
//          "firstName" -> JsString(person.firstName),
//          "lastName" -> JsString(person.lastName),
//          "phoneNumber" -> JsString(person.phoneNumber),
//          "address" -> JsString(person.address)
//        )
//      }
//    }

    Http().bindAndHandle(new MyJsonService().routes, "localhost", 8080)
      .foreach(serverBinding => {
        println(f"Server online at http://localhost:8080")
      })

    Behaviors.ignore
  }

}

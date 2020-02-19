package com.akkapackt.ch1.utils

import akka.actor._

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.akkapackt.ch1.utils.AkkaWriterSystem.Person

import scala.concurrent.ExecutionContext
import java.io.{FileWriter, Writer}

object AkkaWriterSystem extends App {
  case class Person(firstName: String, lastName: String)
  implicit val jsonObject = new JsonWriter[Person] {
    def write(person: Person): JsValue = {
      JsObject(
        "first_name" -> JsString(person.firstName),
        "last_name" -> JsString(person.lastName)
      )
    }
  }
  val system = ActorSystem("WriterSystem");
  val writer = system.actorOf(Props[WriterActor]);
  val personJson = Person(firstName = "Mikael", lastName = "Haug").toJson

  writer ! personJson
}

class WriterActor extends Actor {
  implicit val executionContext: ExecutionContext = executionContext

  def receive = {
    case json => {
      val w: Writer = new FileWriter("./savedProfiles/myJsonFile.json")
      try {
        w.write(json.toString)
      } catch {
        case e: Exception => println(s"could not write to file ${e}")
      }
      finally {
        w.flush()
        w.close()
      }
    }
  }
}

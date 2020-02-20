package com.akkapackt.ch1.utils

import akka.actor._

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.akkapackt.ch1.utils.AkkaWriterSystem.Person

import scala.concurrent.{ExecutionContext, Future}
import java.io.{FileWriter, Writer}

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

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


  implicit val ctx = ActorSystem()
  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/1"), _, _, _) =>
      HttpResponse(entity = "Hello Debasish")

    case HttpRequest(HttpMethods.GET, Uri.Path("/2"), _, _, _) =>
      HttpResponse(entity = "Hello Mikael")

//    case HttpRequest(HttpMethods.POST, Uri.Path("/ping"), _, entity: HttpEntity, _) =>

    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")

  }

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)

      connection handleWithSyncHandler requestHandler
      // this is equivalent to
      // connection handleWith { Flow[HttpRequest] map requestHandler }
    }).run()
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
    case "Pong" => {
      println("Pong!")
    }
  }
}

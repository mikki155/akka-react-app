package com.akkapackt.ch1.utils

import spray.json._

import scala.concurrent.ExecutionContext
import java.io.{FileWriter, Writer}

import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode, Uri}
import akka.stream.scaladsl._
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.common.JsonEntityStreamingSupport
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object AkkaWriterSystem {

  implicit val system = ActorSystem("WriterSystem");
  implicit val executionContext: ExecutionContext = executionContext
  val writer = system.actorOf(Props[WriterActor]);
  val myByteString = ByteString("hello")
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

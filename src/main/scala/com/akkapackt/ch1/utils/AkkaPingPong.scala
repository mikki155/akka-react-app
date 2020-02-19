package com.akkapackt.ch1.utils

import akka.actor._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object AkkaPingPong extends App {
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[PongActor])
  val ping = system.actorOf(Props(new PingActor(pong)))

  ping ! "Start"
}



class PingActor(pongActor: ActorRef) extends Actor {
  implicit val executionContext: ExecutionContext = executionContext
  implicit val timeout: Timeout = Timeout(5.seconds)

  def receive = {
    case "Start" => {
      pongActor ! "Ping"
      println("ping")
//      response.onComplete {
//        case Success(_) => println("Ping-pong successful")
//        case Failure(ex) => println(s"Failed with exception ${ex}")
//      }
    }
  }
}

class PongActor extends Actor {
  def receive = {
    case "Ping" => {
      println("pong")}
  }
}

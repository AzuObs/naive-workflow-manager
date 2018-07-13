package com.naive_workflow.manager

import scala.util.{Failure, Success}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.naive_workflow.manager.actors.{WorkflowExecutionActor, WorkflowActor}
import com.naive_workflow.manager.routes.v1.AbstractV1Routes

import scala.concurrent.duration._

// daniel healthcheckz endpoint
// daniel unit test Services
// daniel try incrementing an execution that doesn't exist? will give a 404
// daniel try starting a workflow that doesn't exist? will give a 404
// daniel db thread pool?
// daniel e2e / API-integration tests?
object ManagerServer extends App with AbstractV1Routes {
  val config = ConfigFactory.load()
  val appName = config.getString("app.name")
  val serverHost = config.getString("server.host")
  val serverPort = config.getInt("server.port")
  val timeoutSeconds = config.getInt("http.timeoutSeconds")

  implicit val system: ActorSystem = ActorSystem("NaiveWorkflowManagerServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(timeoutSeconds.seconds) // daniel env variables

  val workflowActor: ActorRef =
    system.actorOf(WorkflowActor.props, "workflowActor")
  val workflowExecutionActor: ActorRef =
    system.actorOf(WorkflowExecutionActor.props, "workflowExecutionsActor")

  Http()
    .bindAndHandle(routes, serverHost, serverPort)
    .onComplete {
      case Success(_) => println(s"$appName server running at $serverHost:$serverPort")
      case Failure(_) => println(s"$appName server unable to bind at $serverHost:$serverPort")
    }

  // daniel necessary to keep the app alive?
  Await.result(system.whenTerminated, Duration.Inf)
}
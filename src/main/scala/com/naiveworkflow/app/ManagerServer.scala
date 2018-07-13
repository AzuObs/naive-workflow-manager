package com.naiveworkflow.app

import scala.util.{Failure, Success}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scalikejdbc.ConnectionPool
import com.naiveworkflow.app.actors.{WorkflowActor, WorkflowExecutionActor}
import com.naiveworkflow.app.database.{WorkflowDAO, WorkflowExecutionDAO}
import com.naiveworkflow.app.routes.v1.V1Routes
import com.naiveworkflow.app.services.{WorkflowExecutionService, WorkflowService}

object ManagerServer extends App {
  val config = ConfigFactory.load()
  val appName = config.getString("app.name")
  val serverHost = config.getString("server.host")
  val serverPort = config.getInt("server.port")
  val dbUser = config.getString("db.user")
  val dbPass = config.getString("db.pass")
  val dbHost = config.getString("db.host")
  val dbPort = config.getInt("db.port")
  val dbName = config.getString("db.name")
  val timeoutSeconds = config.getInt("http.timeoutSeconds")

  implicit val system: ActorSystem = ActorSystem("NaiveWorkflowManagerServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(timeoutSeconds.seconds)

  ConnectionPool.singleton(s"jdbc:mysql://$dbHost/$dbName?autoReconnect=true&useSSL=false", dbUser, dbPass)

  val blockingDispatcher = system.dispatchers.lookup("blocking-io-dispatcher")
  val workflowDb = WorkflowDAO()(blockingDispatcher)
  val workflowExecutionDb = WorkflowExecutionDAO()(blockingDispatcher)
  val workflowService = WorkflowService(workflowDb)
  val executionService = WorkflowExecutionService(workflowExecutionDb)
  val workflowActor: ActorRef =
    system.actorOf(Props(WorkflowActor(workflowService)), "workflowActor")
  val executionActor: ActorRef =
    system.actorOf(Props(WorkflowExecutionActor(executionService)), "executionActor")
  val routes = V1Routes.routes(workflowActor, executionActor)

  Http()
    .bindAndHandle(routes, serverHost, serverPort)
    .onComplete {
      case Success(_) => println(s"$appName server running at $serverHost:$serverPort")
      case Failure(_) => println(s"$appName server unable to bind at $serverHost:$serverPort")
    }

  Await.result(system.whenTerminated, Duration.Inf)
}

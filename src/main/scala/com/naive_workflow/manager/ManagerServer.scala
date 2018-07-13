package com.naive_workflow.manager

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
import com.naive_workflow.manager.actors.{WorkflowActor, WorkflowExecutionActor}
import com.naive_workflow.manager.routes.v1.AbstractV1Routes
import com.naive_workflow.manager.database.{WorkflowDAO, WorkflowExecutionDAO}

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
  val dbUser = config.getString("db.user")
  val dbPass = config.getString("db.pass")
  val dbHost = config.getString("db.host")
  val dbPort = config.getInt("db.port")
  val dbName = config.getString("db.name")
  val timeoutSeconds = config.getInt("http.timeoutSeconds")

  implicit val system: ActorSystem = ActorSystem("NaiveWorkflowManagerServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(timeoutSeconds.seconds) // daniel env variables
  val blockingDispatcher = system.dispatchers.lookup("blocking-io-dispatcher")

  ConnectionPool.singleton(
    s"jdbc:mysql://$dbHost/$dbName?autoReconnect=true&useSSL=false", dbUser, dbPass)

  // daniel "new" introduces state
  val workflowDb = new WorkflowDAO()(blockingDispatcher)
  val workflowExecutionDb = new WorkflowExecutionDAO()(blockingDispatcher)

  val workflowActor: ActorRef =
    system.actorOf(Props(WorkflowActor(workflowDb)), "workflowActor")
  val workflowExecutionActor: ActorRef =
    system.actorOf(Props(WorkflowExecutionActor(workflowExecutionDb)), "workflowExecutionsActor")

  // daniel look into this whole dispatcher affair
  Http()
    .bindAndHandle(routes, serverHost, serverPort)
    .onComplete {
      case Success(_) => println(s"$appName server running at $serverHost:$serverPort")
      case Failure(_) => println(s"$appName server unable to bind at $serverHost:$serverPort")
    }

  // daniel add "local" (vs "production) to filenames and resource.conf?
  // daniel revisit the interface -> abstract -> class pattern we've been using
  // daniel actor logging?
  // daniel necessary to keep the app alive?
  // daniel what does this do?
  // daniel shutdown delay config?
  // daniel deleted_at not working properly, should be default NULL
  // daniel test everything from scratch
  Await.result(system.whenTerminated, Duration.Inf)
}
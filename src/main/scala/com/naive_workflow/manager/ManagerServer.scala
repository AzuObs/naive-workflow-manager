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
import com.naive_workflow.manager.routes.v1.V1Routes
import com.naive_workflow.manager.database.{WorkflowDAO, WorkflowExecutionDAO}

object ManagerServer extends App with V1Routes {
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

  ConnectionPool.singleton(
    s"jdbc:mysql://$dbHost/$dbName?autoReconnect=true&useSSL=false", dbUser, dbPass)

  val blockingDispatcher = system.dispatchers.lookup("blocking-io-dispatcher")
  val workflowDb = WorkflowDAO()(blockingDispatcher)
  val workflowExecutionDb = WorkflowExecutionDAO()(blockingDispatcher)
  val workflowActor: ActorRef =
    system.actorOf(Props(WorkflowActor(workflowDb)), "workflowsActor")
  val workflowExecutionActor: ActorRef =
    system.actorOf(Props(WorkflowExecutionActor(workflowExecutionDb)), "executionsActor")

  Http()
    .bindAndHandle(routes, serverHost, serverPort)
    .onComplete {
      case Success(_) => println(s"$appName server running at $serverHost:$serverPort")
      case Failure(_) => println(s"$appName server unable to bind at $serverHost:$serverPort")
    }

  // daniel look into this whole dispatcher affair
  // daniel actor logging?
  // daniel test everything from scratch
  // daniel healthcheckz endpoint
  // daniel test docker-compose.test.yml + ./tests.sh
  // daniel Dockerfile and completely working Docker "production"
  // daniel create crontab for scripts
  // daniel unit test Services
  // daniel e2e / API-integration tests?
  // daniel can sbt flag when imports are unused?
  // daniel how on earth does execution context work?
  // daniel eliminate compiling warnings/errors
  // daniel add meaningful comments
  // daniel loggin in general?
  // daniel crontab working
  // daniel function toDateTime(s: String): Datetime, in com.naive_workflow.utils ??
  // daniel every jobs route thoroughly
  // daniel rework the whole git history
  // daniel look at all queries and add indexes
  Await.result(system.whenTerminated, Duration.Inf)
}
package com.naive_workflow.manager.routes.v1

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import com.naive_workflow.manager.services.WorkflowService
import com.naive_workflow.manager.actors.WorkflowActor._
import com.naive_workflow.manager.database.WorkflowDAOInterface
import com.naive_workflow.manager.models.{Workflow, WorkflowJsonSupport, Workflows}

import scala.concurrent.Future

// https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRoutes.scala
// daniel
trait AbstractV1WorkflowRoutes extends WorkflowJsonSupport {

  def workflowActor: ActorRef
  implicit def timeout: Timeout
  implicit def system: ActorSystem

  // daniel - poor error handling
  // daniel - how on earth does execution context work?
  // daniel timeout config
  val v1WorkflowRoutes: Route =
    pathPrefix("workflows") {
      pathEnd {
        get {
          val actorAsk: Future[Future[Workflows]] = (workflowActor ? GetWorkflows).mapTo[Future[Workflows]]
          val actorRes: Future[Workflows] = Await.result(actorAsk, 5.seconds)
          val workflows = Await.result(actorRes, 5.seconds)

          complete(workflows)
        } ~
        post {
          entity(as[Workflow]) {
            workflow =>
              val workflowCreated: Future[Workflow] =
                (workflowActor ? CreateWorkflow).mapTo[Workflow]
              onSuccess(workflowCreated) {
                workflow =>
                  complete((StatusCodes.Created, workflow))
              }}}}}
}

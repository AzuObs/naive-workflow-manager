package com.naive_workflow.manager.routes.v1

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.naive_workflow.manager.actors.WorkflowActor._
import com.naive_workflow.manager.models.{Workflow, WorkflowJsonSupport}

import scala.concurrent.Future

// https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRoutes.scala
// daniel
trait AbstractV1WorkflowRoutes extends WorkflowJsonSupport {

  def workflowActor: ActorRef
  implicit def timeout: Timeout
  implicit def system: ActorSystem

  lazy val v1WorkflowRoutes: Route = // daniel prefix with v1?
    pathPrefix("workflows") {
      pathEnd {
        get {
          val workflows: Future[Workflow] =
            (workflowActor ? GetWorkflows).mapTo[Workflow]
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

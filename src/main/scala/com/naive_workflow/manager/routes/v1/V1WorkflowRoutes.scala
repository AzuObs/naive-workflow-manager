package com.naive_workflow.manager.routes.v1

import scala.concurrent.Await
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes // daniel error handling
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import com.naive_workflow.manager.actors.WorkflowActor._
import com.naive_workflow.manager.models.{
  Workflow,
  Workflows,
  ProposedWorkflow,
  WorkflowJsonSupport
}
import com.naive_workflow.manager.utils.WorkflowUtils

import scala.concurrent.Future

trait V1WorkflowRoutes extends WorkflowJsonSupport {

  def workflowActor: ActorRef

  implicit def system: ActorSystem
  implicit def timeout: Timeout

  val v1WorkflowRoutes: Route =
    pathPrefix("workflows") {
      pathEnd {
        get {
          val actorAsk: Future[Future[Vector[Workflow]]] =
            (workflowActor ? GetWorkflows).mapTo[Future[Vector[Workflow]]]
          val actorRes: Future[Vector[Workflow]] =
            Await.result(actorAsk, timeout.duration)
          val workflows: Vector[Workflow] =
            Await.result(actorRes, timeout.duration)
          val result: Workflows =
            WorkflowUtils.convertWorkflowsTraversableToWorkflowsModel(workflows)

          complete(result)
        } ~
        post {
          entity(as[ProposedWorkflow]) { proposed =>
            val actorAsk: Future[Future[Workflow]] =
              (workflowActor ? CreateWorkflow(proposed)).mapTo[Future[Workflow]]
            val actorRes: Future[Workflow] =
              Await.result(actorAsk, timeout.duration)
            val result: Workflow =
              Await.result(actorRes, timeout.duration)

            complete(result)
          }}}}

}

package com.naive_workflow.manager.routes.v1

import scala.concurrent.{Await, Future}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes // daniel think about these
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import com.naive_workflow.manager.actors.WorkflowExecutionActor._
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation => ProposedIncrementation,
  WorkflowExecutionJsonSupport
}

trait V1WorkflowExecutionRoutes extends WorkflowExecutionJsonSupport {

  def workflowExecutionActor: ActorRef

  implicit def timeout: Timeout
  implicit def system: ActorSystem

  lazy val v1WorkflowExecutionRoutes: Route =
    pathPrefix("workflows" / IntNumber / "executions") { workflowId => {
      pathEnd {
        post {
          val actorAs: Future[Future[WorkflowExecution]] =
            (workflowExecutionActor ? CreateWorkflowExecution(ProposedWorkflowExecution(workflowId)))
              .mapTo[Future[WorkflowExecution]]
          val actorRes: Future[WorkflowExecution] =
            Await.result(actorAs, timeout.duration)
          val workflowExecution: WorkflowExecution =
            Await.result(actorRes, timeout.duration)

          // daniel - giving an incorrect id definitely causes the server to fail, handle error
          complete(workflowExecution)
        }}
    } ~
    pathPrefix("workflows" / IntNumber / "executions" / IntNumber / "incrementations") {
      (workflowId, executionId) =>
        pathEnd {
          post {
            val actorAs: Future[Future[WorkflowExecution]] =
              (workflowExecutionActor ? CreateExecutionIncrementation(ProposedIncrementation(workflowId, executionId)))
                .mapTo[Future[WorkflowExecution]]
            val actorRes: Future[WorkflowExecution] =
              Await.result(actorAs, timeout.duration)
            val workflowExecution: WorkflowExecution =
              Await.result(actorRes, timeout.duration)

            complete(workflowExecution)
          }}}}

}

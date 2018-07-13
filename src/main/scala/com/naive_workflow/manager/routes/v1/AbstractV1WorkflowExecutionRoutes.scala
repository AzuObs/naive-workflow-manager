package com.naive_workflow.manager.routes.v1

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes // daniel think about these
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import com.naive_workflow.manager.actors.WorkflowExecutionActor._
import com.naive_workflow.manager.utils.WorkflowExecutionUtils
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  WorkflowExecutions,
  ProposedWorkflowExecution,
  WorkflowExecutionJsonSupport
}

// daniel https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRoutes.scala
// Abstract ? ...
trait AbstractV1WorkflowExecutionRoutes extends WorkflowExecutionJsonSupport {

  def workflowExecutionActor: ActorRef

  implicit def timeout: Timeout
  implicit def system: ActorSystem

  lazy val v1WorkflowExecutionRoutes: Route =
    pathPrefix("workflows" / IntNumber / "executions") { workflowId => {
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
      } ~
      delete {
        val actorAs: Future[Future[Vector[WorkflowExecution]]] =
          (workflowExecutionActor ? CleanupTerminatedWorkflowExecutions)
            .mapTo[Future[Vector[WorkflowExecution]]]
        val actorRes: Future[Vector[WorkflowExecution]] =
          Await.result(actorAs, timeout.duration)
        val workflowExecutions: Vector[WorkflowExecution] =
          Await.result(actorRes, timeout.duration)
        val result: WorkflowExecutions =
          WorkflowExecutionUtils.executionsTraversableToExecutionsModel(workflowExecutions)

        complete(result)
      }}
    } ~
    pathPrefix("workflows" / IntNumber / "executions" / IntNumber / "incrementations") {
      (workflowId, workflowExecutionId) =>
        post {
          ??? // daniel
//          val workflowIncrementationCreated: Future[WorkflowExecution] =
//            (workflowExecutionActor ? IncrementWorkflowExecution(workflowId, workflowExecutionId))
//              .mapTo[WorkflowExecution]
//          onSuccess(workflowIncrementationCreated) { workflowExecution =>
//            complete(workflowExecution)
          }}
}

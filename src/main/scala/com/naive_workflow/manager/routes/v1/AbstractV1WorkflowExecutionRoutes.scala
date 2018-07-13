package com.naive_workflow.manager.routes.v1

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.naive_workflow.manager.actors.WorkflowExecutionActor._
import com.naive_workflow.manager.models.{WorkflowExecution, WorkflowExecutionJsonSupport}

import scala.concurrent.Future

// daniel https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRoutes.scala
// Abstract ? ...
trait AbstractV1WorkflowExecutionRoutes extends WorkflowExecutionJsonSupport {

  def workflowExecutionActor: ActorRef
  implicit def timeout: Timeout
  implicit def system: ActorSystem

  lazy val v1WorkflowExecutionRoutes: Route =
    pathPrefix("workflows" / IntNumber / "executions") {
      workflowId => {
        post {
          entity(as[WorkflowExecution]) { workflowExecution =>
          val workflowExecutionCreated: Future[WorkflowExecution] =
            (workflowExecutionActor ? CreateWorkflowExecution(workflowId))
              .mapTo[WorkflowExecution]
            onSuccess(workflowExecutionCreated) { workflowExecution =>
              complete((StatusCodes.Created, workflowExecution))
            }
          }
        } ~
        delete {
          val workflowExecutionsDeleted: Future[WorkflowExecution] =
            (workflowExecutionActor ? CleanupTerminatedWorkflowExecutions)
              .mapTo[WorkflowExecution]
          onSuccess(workflowExecutionsDeleted) { workflowExecutions => // daniel should be Vector
            complete((StatusCodes.OK, workflowExecutions))
          }
        }
      }
    } ~
    pathPrefix("workflows" / IntNumber / "executions" / IntNumber / "incrementations") {
      (workflowId, workflowExecutionId) =>
        post {
          val workflowIncrementationCreated: Future[WorkflowExecution] =
            (workflowExecutionActor ? IncrementWorkflowExecution(workflowId, workflowExecutionId))
              .mapTo[WorkflowExecution]
          onSuccess(workflowIncrementationCreated) { workflowExecution =>
            complete((StatusCodes.Created, workflowExecution))
          }}}
}

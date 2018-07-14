package com.naive_workflow.manager.routes.v1

import scala.concurrent.{Await, Future}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout

import com.naive_workflow.manager.utils.WorkflowExecutionUtils
import com.naive_workflow.manager.actors.WorkflowExecutionActor.DeleteEndedWorkflowExecutions
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  WorkflowExecutions,
  WorkflowExecutionJsonSupport
}

// test jobs routes
trait V1JobRoutes extends WorkflowExecutionJsonSupport {

    def workflowExecutionActor: ActorRef

    implicit def timeout: Timeout
    implicit def system: ActorSystem

    lazy val v1JobRoutes: Route =
      pathPrefix("jobs" / "delete-terminated-workflow-executions" ) {
        pathEnd {
          post {
            val actorAs: Future[Future[Vector[WorkflowExecution]]] =
              (workflowExecutionActor ? DeleteEndedWorkflowExecutions)
                .mapTo[Future[Vector[WorkflowExecution]]]
            val actorRes: Future[Vector[WorkflowExecution]] =
              Await.result(actorAs, timeout.duration)
            val workflowExecutions: Vector[WorkflowExecution] =
              Await.result(actorRes, timeout.duration)
            val result: WorkflowExecutions =
              WorkflowExecutionUtils.executionsTraversableToExecutionsModel(workflowExecutions)

            complete(result)
          }}}

}

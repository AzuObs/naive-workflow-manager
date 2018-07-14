package com.naive_workflow.manager.routes.v1

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.naive_workflow.manager.types.ServiceResponse
import com.naive_workflow.manager.utils.WorkflowExecutionUtils
import com.naive_workflow.manager.actors.WorkflowExecutionActor.CreateExecutionsCleanupJob
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  WorkflowExecutionJsonSupport
}

trait V1JobRoutes extends WorkflowExecutionJsonSupport {

    def workflowExecutionActor: ActorRef

    implicit def timeout: Timeout
    implicit def system: ActorSystem

    lazy val v1JobRoutes: Route =
      pathPrefix("jobs" / "delete-terminated-workflow-executions" ) {
        pathEnd {
          post {
            val askService: ServiceResponse[Vector[WorkflowExecution]] =
              for {
                askActor   <-
                  (workflowExecutionActor ? CreateExecutionsCleanupJob)
                    .mapTo[ServiceResponse[Vector[WorkflowExecution]]]
                askService <-
                  askActor
              } yield askService

            Await.result(askService, timeout.duration) match {
              case Right(execs) =>
                val executions = WorkflowExecutionUtils.executionsTraversableToExecutionsModel(execs)
                complete(StatusCodes.Created, executions)
              case Left(exception) =>
                complete(exception.httpStatus, exception.message)
            }
          }}}

}

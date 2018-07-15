package com.naiveworkflow.app.routes.v1

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.utils.WorkflowExecutionUtils
import com.naiveworkflow.app.actors.WorkflowExecutionActor.CreateExecutionsCleanupJob
import com.naiveworkflow.app.models.{
  WorkflowExecution,
  WorkflowExecutionJsonSupport
}

object V1JobRoutes extends WorkflowExecutionJsonSupport {

    def routes(executionActor: ActorRef)(implicit timeout:Timeout): Route =
      pathPrefix("jobs" / "delete-terminated-workflow-executions" ) {
        pathEnd {
          post {
            val askService: ServiceResponse[Vector[WorkflowExecution]] =
              for {
                askActor   <-
                  (executionActor ? CreateExecutionsCleanupJob)
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

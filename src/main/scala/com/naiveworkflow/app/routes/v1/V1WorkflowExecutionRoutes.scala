package com.naiveworkflow.app.routes.v1

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.naiveworkflow.app.utils.{WorkflowExecutionUtils => Utils}
import com.naiveworkflow.app.actors.WorkflowExecutionActor._
import com.naiveworkflow.app.models._
import com.naiveworkflow.app.types.ServiceResponse

trait V1WorkflowExecutionRoutes extends WorkflowExecutionJsonSupport {

  def workflowExecutionActor: ActorRef

  implicit def timeout: Timeout
  implicit def system: ActorSystem

  lazy val v1WorkflowExecutionRoutes: Route =
    pathPrefix("workflows" / IntNumber / "executions") { workflowId => {
      pathEnd {
        get {
          val askService: ServiceResponse[Vector[WorkflowExecution]] =
            for {
              askActor   <- (workflowExecutionActor ? GetWorkflowExecutions(workflowId))
                .mapTo[ServiceResponse[Vector[WorkflowExecution]]]
              askService <- askActor
            } yield askService

          Await.result(askService, timeout.duration) match {
            case Right(execs) =>
              val execusions = Utils.executionsTraversableToExecutionsModel(execs)
              complete(StatusCodes.Created, execusions)
            case Left(exception) =>
              complete(exception.httpStatus, exception.message)
          }
        } ~
        post {
          val askService: ServiceResponse[WorkflowExecution] =
            for {
              askActor   <- (workflowExecutionActor ? CreateWorkflowExecution(ProposedWorkflowExecution(workflowId)))
                .mapTo[ServiceResponse[WorkflowExecution]]
              askService <- askActor
            } yield askService

          Await.result(askService, timeout.duration) match {
            case Right(execution) =>
              complete(StatusCodes.Created, execution)
            case Left(exception) =>
              complete(exception.httpStatus, exception.message)
          }
        }}
    } ~
    pathPrefix("workflows" / IntNumber / "executions" / IntNumber / "incrementations") {
      (workflowId, executionId) =>
        pathEnd {
          post {
            val askService: ServiceResponse[WorkflowExecution] =
              for {
                askActor   <- (workflowExecutionActor ? CreateExecutionIncrementation(
                  ProposedWorkflowExecutionIncrementation(workflowId, executionId)))
                    .mapTo[ServiceResponse[WorkflowExecution]]
                askService <- askActor
              } yield askService

            Await.result(askService, timeout.duration) match {
              case Right(execution) =>
                complete(StatusCodes.Created, execution)
              case Left(exception) =>
                complete(exception.httpStatus, exception.message)
            }
          }}}}

}

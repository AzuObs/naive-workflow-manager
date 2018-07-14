package com.naive_workflow.manager.routes.v1

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.naive_workflow.manager.types.ServiceResponse
import com.naive_workflow.manager.actors.WorkflowActor._
import com.naive_workflow.manager.models._
import com.naive_workflow.manager.utils.{WorkflowUtils => Utils}

trait V1WorkflowRoutes extends WorkflowJsonSupport {

  def workflowActor: ActorRef

  implicit def system: ActorSystem
  implicit def timeout: Timeout

  val v1WorkflowRoutes: Route =
    pathPrefix("workflows") {
      pathEnd {
        get {
          val askService: ServiceResponse[Vector[Workflow]] =
            for {
              askActor   <- (workflowActor ? GetWorkflows).mapTo[ServiceResponse[Vector[Workflow]]]
              askService <- askActor
            } yield askService

          Await.result(askService, timeout.duration) match {
            case Right(wkfls) =>
              val workflows: Workflows = Utils.convertWorkflowsTraversableToWorkflowsModel(wkfls)
              complete(StatusCodes.OK, workflows)
            case Left(exception) => complete(exception.httpStatus, exception.message)
          }
        } ~
        post {
          entity(as[ProposedWorkflow]) { proposed =>
            val askService: ServiceResponse[Workflow] =
              for {
                askActor   <- (workflowActor ? CreateWorkflow).mapTo[ServiceResponse[Workflow]]
                askService <- askActor
              } yield askService

            Await.result(askService, timeout.duration) match {
              case Right(workflow) =>
                complete(StatusCodes.Created, workflow)
              case Left(exception) =>
                complete(exception.httpStatus, exception.message)
            }
          }}}}

}

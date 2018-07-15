package com.naiveworkflow.app.routes.v1

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.actors.WorkflowActor._
import com.naiveworkflow.app.models._
import com.naiveworkflow.app.utils.{WorkflowUtils => Utils}

object V1WorkflowRoutes extends WorkflowJsonSupport {

  def routes(workflowActor: ActorRef)(implicit timeout: Timeout): Route =
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
              val workflows: Workflows = Utils.workflowsTraversableToWorkflowsModel(wkfls)
              complete(StatusCodes.OK, workflows)
            case Left(exception) => complete(exception.httpStatus, exception.message)
          }
        } ~
        post {
          entity(as[ProposedWorkflow]) { proposed =>
            val askService: ServiceResponse[Workflow] =
              for {
                askActor   <- (workflowActor ? CreateWorkflow(proposed)).mapTo[ServiceResponse[Workflow]]
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

package com.naiveworkflow.app.routes.v1

import akka.actor.Props
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}

import scala.concurrent.duration._
import scala.concurrent.Future
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.naiveworkflow.app.Generators._
import com.naiveworkflow.app.actors.WorkflowActor
import com.naiveworkflow.app.models.{Workflow, WorkflowJsonSupport, Workflows}
import com.naiveworkflow.app.services.WorkflowService

class WorkflowRoutesSpec
  extends FlatSpec
    with Matchers
    with MockFactory
    with ScalatestRouteTest
    with WorkflowJsonSupport {

  "GET /workflows " should
    "returns (200, Workflows)" in {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowService]
      val actor = system.actorOf(Props(WorkflowActor(service)), genUUID)
      val routes = V1WorkflowRoutes.routes(actor)
      val workflows = genMultipleWorkflow()

      service.getWorkflows _ expects() returns Future { Right(workflows) }

      Get("/workflows") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Workflows] shouldEqual Workflows(workflows)
      }}

  "POST /workflows " should
    "returns (201, Workflow)" in {
    implicit val timeout: Timeout = Timeout(5.seconds)
    val service = mock[WorkflowService]
    val actor = system.actorOf(Props(WorkflowActor(service)), genUUID)
    val routes = V1WorkflowRoutes.routes(actor)
    val nSteps = 3
    val workflow = genSingleWorkflow(nSteps)
    val proposed = genProposedWorkflow(nSteps)

    service.createWorkflow _ expects proposed returns Future { Right(workflow) }

    Post("/workflows", proposed) ~> routes ~> check {
      status shouldEqual StatusCodes.Created
      responseAs[Workflow] shouldEqual workflow
    }}

}

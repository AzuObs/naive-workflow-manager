package com.naiveworkflow.app.routes.v1

import akka.actor.Props
import akka.http.scaladsl.model.StatusCodes

import scala.concurrent.duration._
import scala.concurrent.Future
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.naiveworkflow.app.Fixtures._
import com.naiveworkflow.app.actors.WorkflowExecutionActor
import com.naiveworkflow.app.models.{WorkflowExecution, WorkflowExecutionJsonSupport, WorkflowExecutions}
import com.naiveworkflow.app.services.WorkflowExecutionService

class WorkflowExecutionRoutesSpec
  extends FlatSpec
    with Matchers
    with MockFactory
    with ScalatestRouteTest
    with WorkflowExecutionJsonSupport {

  val workflowId: Int = 123
  
  s"GET /workflows/$workflowId/executions" should
    "returns (200, WorkflowExecutions)" in {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowExecutionService]
      val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
      val routes = V1WorkflowExecutionRoutes.routes(actor)
      val executions = genMultipleWorkflowExecution(workflowId)
  
      service.getWorkflowExecutions _ expects workflowId returns Future { Right(executions) }
  
      Get(s"/workflows/$workflowId/executions") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[WorkflowExecutions] shouldEqual WorkflowExecutions(executions)
      }}

  s"POST /workflows/$workflowId/executions" should
    "returns (201, WorkflowExecution)" in {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowExecutionService]
      val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
      val routes = V1WorkflowExecutionRoutes.routes(actor)
      val execution = genSingleWorkflowExecution(workflowId)
      val proposed = genProposedWorkflowExecution(workflowId)

      service.createWorkflowExecution _ expects proposed returns Future { Right(execution) }

      Post(s"/workflows/$workflowId/executions") ~> routes ~> check {
        status shouldEqual StatusCodes.Created
        responseAs[WorkflowExecution] shouldEqual execution
      }}

  val workflowExecutionId = 456

  s"POST /workflows/$workflowId/executions/$workflowExecutionId/incrementations" should
    "returns (201, WorkflowExecution)" in {
    implicit val timeout: Timeout = Timeout(5.seconds)
    val service = mock[WorkflowExecutionService]
    val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
    val routes = V1WorkflowExecutionRoutes.routes(actor)
    val execution = genSingleWorkflowExecution(workflowId)
    val proposed = genProposedWorkflowExecutionIncrementation(workflowExecutionId, workflowId)

    service.incrementWorkflowExecution _ expects proposed returns Future { Right(execution) }

    Post(s"/workflows/$workflowId/executions/$workflowExecutionId/incrementations") ~> routes ~> check {
      status shouldEqual StatusCodes.Created
      responseAs[WorkflowExecution] shouldEqual execution
    }}

}

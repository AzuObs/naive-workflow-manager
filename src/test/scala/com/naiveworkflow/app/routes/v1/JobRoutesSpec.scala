package com.naiveworkflow.app.routes.v1

import akka.actor.Props
import akka.http.scaladsl.model.StatusCodes

import scala.concurrent.duration._
import scala.concurrent.Future
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.naiveworkflow.app.Generators._
import com.naiveworkflow.app.actors.WorkflowExecutionActor
import com.naiveworkflow.app.models.{WorkflowExecution, WorkflowExecutionJsonSupport, WorkflowExecutions}
import com.naiveworkflow.app.services.WorkflowExecutionService

class JobRoutesSpec
  extends FlatSpec
    with Matchers
    with MockFactory
    with ScalatestRouteTest
    with WorkflowExecutionJsonSupport {

  val workflowId: Int = 123

  s"POST /jobs/delete-terminated-workflow-executions" should
    "returns (201, WorkflowExecution)" in {
    implicit val timeout: Timeout = Timeout(5.seconds)
    val service = mock[WorkflowExecutionService]
    val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
    val routes = V1JobRoutes.routes(actor)
    val executions = genMultipleWorkflowExecution(n = 0)

    service.deletedEndedWorkflowExecutions _ expects() returns Future { Right(executions) }

    Post(s"/jobs/delete-terminated-workflow-executions") ~> routes ~> check {
      status shouldEqual StatusCodes.Created
      responseAs[WorkflowExecutions] shouldEqual WorkflowExecutions(executions)
    }}

}

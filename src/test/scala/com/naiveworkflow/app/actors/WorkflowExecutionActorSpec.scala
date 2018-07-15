package com.naiveworkflow.app.actors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.pattern.ask
import akka.util.Timeout
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import com.naiveworkflow.app.services.WorkflowExecutionService
import com.naiveworkflow.app.Generators._
import com.naiveworkflow.app.actors.WorkflowExecutionActor._

class WorkflowExecutionActorSpec
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with FlatSpecLike
    with Matchers
    with MockFactory
    with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "WorkflowActor.GetWorkflowExecutions" should
    "calls and returns workflow service getWorkflows result" in  {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowExecutionService]
      val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
      val workflowId = genId
      val res = Future { Right(genMultipleWorkflowExecution()) }

      service.getWorkflowExecutions _ expects workflowId returns res

      Await.result(actor ? GetWorkflowExecutions(workflowId), 5.seconds) should equal(res)
    }

  "WorkflowActor.CreateWorkflowExecution" should
    "calls and returns workflow service createWorkflowExecution result" in  {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowExecutionService]
      val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
      val proposed = genProposedWorkflowExecution()
      val res = Future { Right(genSingleWorkflowExecution()) }

      service.createWorkflowExecution _ expects proposed returns res

      Await.result(actor ? CreateWorkflowExecution(proposed), 5.seconds) should equal(res)
    }

  "WorkflowActor.CreateExecutionIncrementation" should
    "calls and returns workflow service incrementWorkflowExecution result" in  {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowExecutionService]
      val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
      val proposed = genProposedWorkflowExecutionIncrementation()
      val res = Future { Right(genSingleWorkflowExecution()) }

      service.incrementWorkflowExecution _ expects proposed returns res

      Await.result(actor ? CreateExecutionIncrementation(proposed), 5.seconds) should equal(res)
    }

  "WorkflowActor.CreateExecutionsCleanupJob" should
    "calls and returns workflow service deletedEndedWorkflowExecutions result" in  {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowExecutionService]
      val actor = system.actorOf(Props(WorkflowExecutionActor(service)), genUUID)
      val res = Future { Right(genMultipleWorkflowExecution()) }

      service.deletedEndedWorkflowExecutions _ expects() returns res

      Await.result(actor ? CreateExecutionsCleanupJob, 5.seconds) should equal(res)
    }

}

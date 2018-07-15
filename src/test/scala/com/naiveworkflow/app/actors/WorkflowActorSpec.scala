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
import com.naiveworkflow.app.services.WorkflowService
import com.naiveworkflow.app.Fixtures._
import com.naiveworkflow.app.actors.WorkflowActor.{CreateWorkflow, GetWorkflows}

class WorkflowActorSpec
  extends TestKit(ActorSystem(genUUID))
    with ImplicitSender
    with FlatSpecLike
    with Matchers
    with MockFactory
    with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "WorkflowActor.GetWorkflows" should
    "calls and returns workflow service getWorkflows result" in  {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowService]
      val actor = system.actorOf(Props(WorkflowActor(service)), genUUID)
      val res = Future { Right(genMultipleWorkflow()) }

      service.getWorkflows _ expects() returns res

      Await.result(actor ? GetWorkflows, 5.seconds) should equal(res)
    }

  "WorkflowActor.CreateWorkflow" should
    "calls and returns workflow service CreateWorkflow result" in  {
      implicit val timeout: Timeout = Timeout(5.seconds)
      val service = mock[WorkflowService]
      val actor = system.actorOf(Props(WorkflowActor(service)), genUUID)
      val proposed = genProposedWorkflow()
      val res = Future { Right(genSingleWorkflow()) }

      service.createWorkflow _ expects proposed returns res

      Await.result(actor ? CreateWorkflow(proposed), 5.seconds) should equal(res)
    }

}

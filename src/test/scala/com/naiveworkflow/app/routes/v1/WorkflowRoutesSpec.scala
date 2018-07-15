//package com.naiveworkflow.app.routes.v1
//
//import scala.concurrent.duration._
//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
//import akka.util.Timeout
//import akka.http.scaladsl.model.StatusCodes
//import akka.http.scaladsl.testkit.ScalatestRouteTest
//import akka.http.scaladsl.server._
//import Directives._
//import akka.actor.{ActorRef, ActorSystem, Props}
//import org.scalatest._
//import org.scalamock.scalatest.MockFactory
//import com.naiveworkflow.app.routes.v1.V1WorkflowRoutes
//import com.naiveworkflow.app.Generators._
//import com.naiveworkflow.app.ManagerServer.{blockingDispatcher, system, workflowDb}
//import com.naiveworkflow.app.actors.WorkflowActor
//import com.naiveworkflow.app.database.WorkflowDAO
//import com.naiveworkflow.app.models.Workflow
//import com.naiveworkflow.app.types.ServiceResponse
//
//class WorkflowRoutesSpec
//  extends FlatSpec
//    with Matchers
//    with ScalatestRouteTest
//    with MockFactory {
//
//  object WorkflowRoutes extends V1WorkflowRoutes {
//    val timeout: Timeout = Timeout(5.seconds)
//    val system = ActorSystem("NaiveWorkflowManagerServer")
//    val workflowDb = WorkflowDAO()
//    val workflowActor: ActorRef =
//      system.actorOf(Props(WorkflowActor(workflowDb)), "workflowsActor")
//  }
//
//  "foo" should
//    "bar" in  {
//      Get() ~> WorkflowRoutes.v1WorkflowRoutes ~> check {
//        ???
////        responseAs[Future[ServiceResponse[Vector[Workflow]]]] shouldEqual
//      }
//    }
//
//  it should
//    "bar2" in  {
//      assert(true)
//    }
//
//}
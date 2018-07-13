package com.naive_workflow.manager.routes.v1

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import com.naive_workflow.manager.actors.WorkflowActor._
import com.naive_workflow.manager.models.{
  Workflow,
  Workflows,
  ProposedWorkflow,
  WorkflowJsonSupport
}
import com.naive_workflow.manager.utils.WorkflowUtils

import scala.concurrent.Future

// https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRoutes.scala
// daniel
trait AbstractV1WorkflowRoutes extends WorkflowJsonSupport {

  def workflowActor: ActorRef

  implicit def system: ActorSystem
  implicit def timeout: Timeout

  // daniel - poor error handling
  // daniel - how on earth does execution context work?
  // daniel timeout config
  val v1WorkflowRoutes: Route =
    pathPrefix("workflows") {
      pathEnd {
        get {
          val actorAsk: Future[Future[Vector[Workflow]]] =
            (workflowActor ? GetWorkflows).mapTo[Future[Vector[Workflow]]]
          val actorRes: Future[Vector[Workflow]] =
            Await.result(actorAsk, 5.seconds)
          val workflows: Vector[Workflow] =
            Await.result(actorRes, 5.seconds)
          val result: Workflows =
            WorkflowUtils.convertWorkflowsTraversableToWorkflowsModel(workflows)

          complete(result)
        } ~
        post {
          entity(as[ProposedWorkflow]) { proposed =>
            val actorAsk: Future[Future[Workflow]] =
              (workflowActor ? CreateWorkflow(proposed)).mapTo[Future[Workflow]]
            val actorRes: Future[Workflow] =
              Await.result(actorAsk, 5.seconds)
            val result: Workflow =
              Await.result(actorRes, 5.seconds)

            complete(result)
          }
        }
      }
    }
}

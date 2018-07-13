package com.naive_workflow.manager.actors

import scala.util.{Failure, Success} // daniel
import akka.actor.{Actor, Props}

import com.naive_workflow.manager.database.WorkflowDAOInterface
import com.naive_workflow.manager.services.WorkflowService

object WorkflowActor {
  final case object GetWorkflows
  final case class CreateWorkflow(nSteps: Int)

  def props: Props = Props[WorkflowActor]
}

// daniel https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRegistryActor.scala

// daniel implicit ???
// daniel ask pattern?
case class WorkflowActor(db: WorkflowDAOInterface) extends Actor {
  import WorkflowActor._

  def receive: Receive = {
    case GetWorkflows => {
      import scala.concurrent.ExecutionContext.Implicits.global // daniel
      sender() ! WorkflowService(db)
        .getWorkflows
//        .onComplete {
//          case Success(v) =>
//            println(s"Actor: $v\n")
//            v
//          case Failure(e) =>
//            e
//        }
    }
    case CreateWorkflow =>
      sender() ! ??? // daniel
  }
}
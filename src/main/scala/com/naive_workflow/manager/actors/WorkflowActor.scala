package com.naive_workflow.manager.actors

import akka.actor.{Actor, Props}

import com.naive_workflow.manager.models.Workflow

object WorkflowActor {
  final case object GetWorkflows
  final case class CreateWorkflow(nSteps: Int)

  def props: Props = Props[WorkflowActor]
}

// daniel https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRegistryActor.scala
class WorkflowActor extends Actor {
  import WorkflowActor._

  // daniel Services go here?
  def receive: Receive = {
    case GetWorkflows =>
      sender() ! ??? // daniel
    case CreateWorkflow =>
      sender() ! ??? // daniel
  }
}
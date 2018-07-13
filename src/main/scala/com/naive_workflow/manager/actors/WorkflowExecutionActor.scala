package com.naive_workflow.manager.actors

import akka.actor.{Actor, Props}

import com.naive_workflow.manager.models.WorkflowExecution

object WorkflowExecutionActor {
  final case class CreateWorkflowExecution(workflowId: Int)
  final case class IncrementWorkflowExecution(workflowId: Int, workflowExecutionId: Int)
  final case object CleanupTerminatedWorkflowExecutions

  def props: Props = Props[WorkflowExecutionActor]
}

// daniel "Registry" is unecessary
// daniel https://github.com/akka/akka-http-quickstart-scala.g8/blob/10.1.x/src/main/g8/src/main/scala/%24package%24/UserRegistryActor.scala
class WorkflowExecutionActor extends Actor {
  import WorkflowExecutionActor._

  def receive: Receive = {
    case CreateWorkflowExecution(workflowId) =>
      sender() ! ??? // daniel
    case IncrementWorkflowExecution(workflowId, workflowExecutionId) =>
      sender() ! ??? // daniel
    case CleanupTerminatedWorkflowExecutions =>
      sender() ! ??? // daniel
  }
}
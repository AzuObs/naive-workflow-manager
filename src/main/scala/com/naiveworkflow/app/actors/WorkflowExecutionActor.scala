package com.naiveworkflow.app.actors

import akka.actor.{Actor, Props}
import com.naiveworkflow.app.services.WorkflowExecutionService
import com.naiveworkflow.app.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation => ProposedIncrementation
}

object WorkflowExecutionActor {

  final case class GetWorkflowExecutions(workflowId: Int)
  final case class CreateWorkflowExecution(proposed: ProposedWorkflowExecution)
  final case class CreateExecutionIncrementation(proposed: ProposedIncrementation)
  final case object CreateExecutionsCleanupJob

  def props: Props = Props[WorkflowExecutionActor]

}

case class WorkflowExecutionActor(service: WorkflowExecutionService) extends Actor {
  import WorkflowExecutionActor._

  def receive: Receive = {
    case GetWorkflowExecutions(workflowId) =>
      sender() ! service.getWorkflowExecutions(workflowId)
    case CreateWorkflowExecution(proposed) =>
      sender() ! service.createWorkflowExecution(proposed)
    case CreateExecutionIncrementation(proposed) =>
      sender() ! service.incrementWorkflowExecution(proposed)
    case CreateExecutionsCleanupJob =>
      sender() ! service.deletedEndedWorkflowExecutions
  }

}

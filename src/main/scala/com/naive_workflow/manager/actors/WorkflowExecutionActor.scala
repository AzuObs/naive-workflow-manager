package com.naive_workflow.manager.actors

import akka.actor.{Actor, Props}

import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface
import com.naive_workflow.manager.services.WorkflowExecutionService
import com.naive_workflow.manager.models.ProposedWorkflowExecution

object WorkflowExecutionActor {
  final case class CreateWorkflowExecution(proposedExecution: ProposedWorkflowExecution)
  final case class IncrementWorkflowExecution(workflowId: Int, workflowExecutionId: Int)
  final case object CleanupTerminatedWorkflowExecutions

  def props: Props = Props[WorkflowExecutionActor]
}

case class WorkflowExecutionActor(db: WorkflowExecutionDAOInterface) extends Actor {
  import WorkflowExecutionActor._

  def receive: Receive = {
    case CreateWorkflowExecution(proposedExecution) =>
      sender() ! WorkflowExecutionService(db).createWorkflowExecution(proposedExecution)
    case IncrementWorkflowExecution(workflowId, workflowExecutionId) =>
      sender() ! ???
    case CleanupTerminatedWorkflowExecutions =>
      sender() ! WorkflowExecutionService(db).cleanupTerminatedWorkflowExecutions
  }
}
package com.naiveworkflow.app.actors

import akka.actor.{Actor, Props}
import com.naiveworkflow.app.database.WorkflowExecutionDAO
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

case class WorkflowExecutionActor(db: WorkflowExecutionDAO) extends Actor {
  import WorkflowExecutionActor._

  def receive: Receive = {
    case GetWorkflowExecutions(workflowId) =>
      sender() ! WorkflowExecutionService(db).getWorkflowExecutions(workflowId)
    case CreateWorkflowExecution(proposed) =>
      sender() ! WorkflowExecutionService(db).createWorkflowExecution(proposed)
    case CreateExecutionIncrementation(proposed) =>
      sender() ! WorkflowExecutionService(db).incrementWorkflowExecution(proposed)
    case CreateExecutionsCleanupJob =>
      sender() ! WorkflowExecutionService(db).deletedEndedWorkflowExecutions
  }

}

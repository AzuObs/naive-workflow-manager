package com.naive_workflow.manager.actors

import akka.actor.{Actor, Props}
import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface
import com.naive_workflow.manager.services.WorkflowExecutionService
import com.naive_workflow.manager.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation => ProposedIncrementation
}

// daniel should these maybe not be given straight form the TOP down and created in service?
object WorkflowExecutionActor {
  final case class CreateWorkflowExecution(proposed: ProposedWorkflowExecution)
  final case class CreateExecutionIncrementation(proposed: ProposedIncrementation)
  final case object CreateExecutionsCleanupJob

  def props: Props = Props[WorkflowExecutionActor]
}

case class WorkflowExecutionActor(db: WorkflowExecutionDAOInterface) extends Actor {
  import WorkflowExecutionActor._

  def receive: Receive = {
    case CreateWorkflowExecution(proposed) =>
      sender() ! WorkflowExecutionService(db).createWorkflowExecution(proposed)
    case CreateExecutionIncrementation(proposed) =>
      sender() ! WorkflowExecutionService(db).incrementWorkflowExecution(proposed)
    case CreateExecutionsCleanupJob =>
      sender() ! WorkflowExecutionService(db).deletedEndedWorkflowExecutions
  }

}

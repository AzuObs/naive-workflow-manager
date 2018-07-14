package com.naive_workflow.manager.database

import scala.concurrent.Future
import scala.collection.immutable.Vector
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation
}

trait WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution):
    Future[WorkflowExecution]
  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    Future[WorkflowExecution]
  def getTerminatedWorkflowExecutions:
    Future[Vector[WorkflowExecution]]
  def deleteWorkflowExecutions(workflowExecutions: Vector[WorkflowExecution]):
    Future[Vector[WorkflowExecution]]

}
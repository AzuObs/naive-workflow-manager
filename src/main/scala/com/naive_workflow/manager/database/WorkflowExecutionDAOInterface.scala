package com.naive_workflow.manager.database

import scala.collection.immutable.Vector

import com.naive_workflow.manager.types.DAOResponse
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation
}

trait WorkflowExecutionDAOInterface {

  def getTerminatedWorkflowExecutions:
    DAOResponse[Vector[WorkflowExecution]]

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution):
    DAOResponse[WorkflowExecution]

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    DAOResponse[WorkflowExecution]

  def deleteWorkflowExecutions(workflowExecutions: Vector[WorkflowExecution]):
    DAOResponse[Vector[WorkflowExecution]]

}

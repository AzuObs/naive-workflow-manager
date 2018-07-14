package com.naiveworkflow.app.database

import scala.collection.immutable.Vector

import com.naiveworkflow.app.types.DAOResponse
import com.naiveworkflow.app.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation
}

trait WorkflowExecutionDAOInterface {

  def getWorkflowExecutions(workflowId: Int):
    DAOResponse[Vector[WorkflowExecution]]

  def getTerminatedWorkflowExecutions:
    DAOResponse[Vector[WorkflowExecution]]

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution):
    DAOResponse[WorkflowExecution]

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    DAOResponse[WorkflowExecution]

  def deleteWorkflowExecutions(workflowExecutions: Vector[WorkflowExecution]):
    DAOResponse[Vector[WorkflowExecution]]

}

package com.naiveworkflow.app.services

import scala.collection.immutable.Vector

import com.naiveworkflow.app.database.WorkflowExecutionDAOInterface
import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation,
  WorkflowExecution
}

trait WorkflowExecutionServiceInterface {

  protected val database: WorkflowExecutionDAOInterface

  def getWorkflowExecutions(workflowId: Int):
    ServiceResponse[Vector[WorkflowExecution]]

  def createWorkflowExecution(proposed: ProposedWorkflowExecution):
    ServiceResponse[WorkflowExecution]

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    ServiceResponse[WorkflowExecution]

  def deletedEndedWorkflowExecutions:
    ServiceResponse[Vector[WorkflowExecution]]

}

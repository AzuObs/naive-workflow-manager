package com.naive_workflow.manager.services

import scala.collection.immutable.Vector

import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface
import com.naive_workflow.manager.types.ServiceResponse
import com.naive_workflow.manager.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation,
  WorkflowExecution
}

trait WorkflowExecutionServiceInterface {

  protected def database: WorkflowExecutionDAOInterface

  def createWorkflowExecution(proposed: ProposedWorkflowExecution):
    ServiceResponse[WorkflowExecution]

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    ServiceResponse[WorkflowExecution]

  def deletedEndedWorkflowExecutions:
    ServiceResponse[Vector[WorkflowExecution]]

}

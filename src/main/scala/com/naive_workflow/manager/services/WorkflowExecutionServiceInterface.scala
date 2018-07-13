package com.naive_workflow.manager.services

import com.naive_workflow.manager.models.WorkflowExecution
import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface

trait WorkflowExecutionServiceInterface {

  protected def database: WorkflowExecutionDAOInterface

  def createWorkflowExecution(workflowId: Int): WorkflowExecution
  def incrementWorkflowExecution(workflowExecutionId: Int): WorkflowExecution
  def deleteTerminatedWorkflowExecutions: WorkflowExecution

}

package com.naive_workflow.manager.services

import com.naive_workflow.manager.models.WorkflowExecution

trait AbstractWorkflowExecutionService extends WorkflowExecutionServiceInterface {

  def createWorkflowExecution(workflowId: Int): WorkflowExecution = ???
  def incrementWorkflowExecution(workflowExecutionId: Int): WorkflowExecution = ???
  def deleteTerminatedWorkflowExecutions: WorkflowExecution = ???

}

package com.naive_workflow.manager.services

import scala.concurrent.Future
import scala.collection.immutable.Vector
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation
}
import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface

trait WorkflowExecutionServiceInterface {

  protected def database: WorkflowExecutionDAOInterface

  def createWorkflowExecution(proposed: ProposedWorkflowExecution): Future[WorkflowExecution]
  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation): Future[WorkflowExecution]
  def cleanupTerminatedWorkflowExecutions: Future[Vector[WorkflowExecution]]

}

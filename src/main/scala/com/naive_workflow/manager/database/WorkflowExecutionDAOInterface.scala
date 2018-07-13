package com.naive_workflow.manager.database

import scala.concurrent.Future
import scala.collection.immutable.Vector

import com.naive_workflow.manager.models.{ProposedWorkflowExecution, WorkflowExecution}

trait WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution):
    Future[WorkflowExecution]
  def updateWorkflowExecution(workflowExecutionId: Int, currentStepIndex: Int):
    Future[WorkflowExecution]
  def getTerminatedWorkflowExecutions:
    Future[Vector[WorkflowExecution]]
  def deleteWorkflowExecutions(workflowExecutions: Vector[WorkflowExecution]):
    Future[Vector[WorkflowExecution]]

}
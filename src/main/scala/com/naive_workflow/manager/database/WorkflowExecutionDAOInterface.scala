package com.naive_workflow.manager.database

import scala.collection.immutable.Vector

import com.naive_workflow.IO
import com.naive_workflow.manager.models.WorkflowExecution

trait WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(workflowId: Int): IO[WorkflowExecution]
  def updateWorkflowExecution(workflowExecutionId: Int, currentStepIndex: Int): IO[WorkflowExecution]
  def deleteWorkflowExecutions(workflowExecutionIds: Vector[Int]): IO[Vector[WorkflowExecution]]

}
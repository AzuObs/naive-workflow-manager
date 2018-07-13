package com.naive_workflow.manager.database

import com.naive_workflow.IO
import com.naive_workflow.manager.models.WorkflowExecution

import scala.collection.immutable.Vector

object WorkflowExecutionDAO extends WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(workflowId: Int)
    : IO[WorkflowExecution]
    = ???
  def updateWorkflowExecution(workflowExecutionId: Int, currentStepIndex: Int)
    : IO[WorkflowExecution]
    = ???
  def deleteWorkflowExecutions(workflowExecutionIds: Vector[Int])
    : IO[Vector[WorkflowExecution]]
    = ???

}
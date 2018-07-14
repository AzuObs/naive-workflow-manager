package com.naive_workflow.manager.services

import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Vector
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation
}

case class WorkflowExecutionService(db: WorkflowExecutionDAOInterface)
  extends WorkflowExecutionServiceInterface {

  def database: WorkflowExecutionDAOInterface = db

  def createWorkflowExecution(proposed: ProposedWorkflowExecution):
    Future[WorkflowExecution] =
      database.insertWorkflowExecution(proposed)

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    Future[WorkflowExecution] =
      database.incrementWorkflowExecution(proposed)

  def deletedEndedWorkflowExecutions: Future[Vector[WorkflowExecution]] =
    // daniel working OK? how about error handling?
    // daniel probably make this as a single DAO call, making it less error prone
    for {
      getExecutions  <- database.getTerminatedWorkflowExecutions
      delExecutions  <- database.deleteWorkflowExecutions(getExecutions)
    } yield delExecutions

}

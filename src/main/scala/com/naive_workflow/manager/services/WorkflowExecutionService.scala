package com.naive_workflow.manager.services

import scala.collection.immutable.Vector

import com.naive_workflow.manager.database.WorkflowExecutionDAOInterface
import com.naive_workflow.manager.types.ServiceResponse
import com.naive_workflow.manager.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation,
  WorkflowExecution
}

case class WorkflowExecutionService(db: WorkflowExecutionDAOInterface)
  extends WorkflowExecutionServiceInterface {

  def database: WorkflowExecutionDAOInterface = db

  def createWorkflowExecution(proposed: ProposedWorkflowExecution):
    ServiceResponse[WorkflowExecution] =
      database.insertWorkflowExecution(proposed)

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    ServiceResponse[WorkflowExecution] =
      database.incrementWorkflowExecution(proposed)

  // daniel how does error handling behave? use .fold instead?
  def deletedEndedWorkflowExecutions: ServiceResponse[Vector[WorkflowExecution]] = ???
//    for {
//      ioExecutions   <- database.getTerminatedWorkflowExecutions
//      executions     <- ioExecutions
//      delExecutions  <- database.deleteWorkflowExecutions(executions)
//    } yield delExecutions

}

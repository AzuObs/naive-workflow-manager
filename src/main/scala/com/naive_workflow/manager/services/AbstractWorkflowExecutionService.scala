package com.naive_workflow.manager.services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Vector
import com.naive_workflow.manager.models.{
  WorkflowExecution,
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation,
}
// daniel can sbt flag when imports are stale?
trait AbstractWorkflowExecutionService extends WorkflowExecutionServiceInterface {

  def createWorkflowExecution(proposed: ProposedWorkflowExecution): Future[WorkflowExecution] =
    database.insertWorkflowExecution(proposed)
  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation): Future[WorkflowExecution] =
    database.incrementWorkflowExecution(proposed)
  def cleanupTerminatedWorkflowExecutions: Future[Vector[WorkflowExecution]] =
    // daniel working OK? how about error handling?
  // daniel probably make this as a single DAO call, makes it less error prone
    for {
      getExecutions  <- database.getTerminatedWorkflowExecutions
      delExecutions  <- database.deleteWorkflowExecutions(getExecutions)
    } yield delExecutions

}

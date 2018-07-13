package com.naive_workflow.manager.services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Vector

import com.naive_workflow.manager.models.{ProposedWorkflowExecution, WorkflowExecution}
// daniel can sbt flag when imports are stale?
trait AbstractWorkflowExecutionService extends WorkflowExecutionServiceInterface {

  def createWorkflowExecution(proposed: ProposedWorkflowExecution): Future[WorkflowExecution] =
    database.insertWorkflowExecution(proposed)
  def incrementWorkflowExecution(workflowExecutionId: Int): Future[WorkflowExecution] =
    database.updateWorkflowExecution(1, 2) // daniel
  def cleanupTerminatedWorkflowExecutions: Future[Vector[WorkflowExecution]] =
    // daniel working OK? how about error handling?
    for {
      getExecutions  <- database.getTerminatedWorkflowExecutions
      delExecutions  <- database.deleteWorkflowExecutions(getExecutions)
    } yield delExecutions

}

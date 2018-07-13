package com.naiveworkflow.app.services

import scala.collection.immutable.Vector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.naiveworkflow.app.database.WorkflowExecutionDAOInterface
import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation,
  WorkflowExecution
}

case class WorkflowExecutionService(db: WorkflowExecutionDAOInterface)
  extends WorkflowExecutionServiceInterface {

  protected lazy val database: WorkflowExecutionDAOInterface = db

  def getWorkflowExecutions(workflowId: Int):
  ServiceResponse[Vector[WorkflowExecution]] =
    database.getAllWorkflowExecutions(workflowId)

  def createWorkflowExecution(proposed: ProposedWorkflowExecution):
  ServiceResponse[WorkflowExecution] =
    database.insertWorkflowExecution(proposed)

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
  ServiceResponse[WorkflowExecution] =
    database.incrementWorkflowExecution(proposed)

  def deletedEndedWorkflowExecutions:
  ServiceResponse[Vector[WorkflowExecution]] =
    database.getTerminatedWorkflowExecutions flatMap {
      case Right(workflows) => database.deleteWorkflowExecutions(workflows)
      case Left(exception) => Future.successful(Left(exception))
    }

}

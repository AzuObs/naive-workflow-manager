package com.naiveworkflow.app.services

import scala.concurrent.Future
import com.naiveworkflow.app.database.WorkflowDAOInterface
import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.models.{
  ProposedWorkflow,
  Workflow,
  BusinessStepsTooSmall,
  BusinessStepsTooBig
}

case class WorkflowService(db: WorkflowDAOInterface)
  extends WorkflowServiceInterface {

  protected lazy val database: WorkflowDAOInterface = db

  def getWorkflows: ServiceResponse[Vector[Workflow]] =
    database.getAllWorkflows

  // daniel add tests for this
  def createWorkflow(proposed: ProposedWorkflow): ServiceResponse[Workflow] =
    proposed.nSteps match {
      case gt0 if gt0 < 1 =>
        Future.successful(Left(BusinessStepsTooSmall()))
      case max if max > 1000 =>
        Future.successful(Left(BusinessStepsTooBig()))
      case _ => database.insertWorkflow(proposed)
    }

}

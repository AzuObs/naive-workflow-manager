package com.naiveworkflow.app.services

import com.naiveworkflow.app.database.WorkflowDAOInterface
import com.naiveworkflow.app.models.{ProposedWorkflow, Workflow}
import com.naiveworkflow.app.types.ServiceResponse

case class WorkflowService(db: WorkflowDAOInterface)
  extends WorkflowServiceInterface {

  protected lazy val database: WorkflowDAOInterface = db

  def getWorkflows: ServiceResponse[Vector[Workflow]] =
    database.getAllWorkflows

  def createWorkflow(proposed: ProposedWorkflow): ServiceResponse[Workflow] =
    database.insertWorkflow(proposed)

}

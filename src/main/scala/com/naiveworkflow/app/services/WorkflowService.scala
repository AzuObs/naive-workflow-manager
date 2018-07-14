package com.naiveworkflow.app.services

import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.database.WorkflowDAOInterface
import com.naiveworkflow.app.models.{ProposedWorkflow, Workflow}

case class WorkflowService(db: WorkflowDAOInterface) extends WorkflowServiceInterface {

  def database: WorkflowDAOInterface = db

  def createWorkflow(proposed: ProposedWorkflow): ServiceResponse[Workflow] =
    database.insertWorkflow(proposed)

  def getWorkflows: ServiceResponse[Vector[Workflow]] =
    database.getAllWorkflows

}

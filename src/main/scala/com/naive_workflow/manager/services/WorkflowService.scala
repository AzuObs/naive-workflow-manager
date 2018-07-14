package com.naive_workflow.manager.services

import com.naive_workflow.manager.types.ServiceResponse
import com.naive_workflow.manager.database.WorkflowDAOInterface
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

case class WorkflowService(db: WorkflowDAOInterface) extends WorkflowServiceInterface {

  def database: WorkflowDAOInterface = db

  def createWorkflow(proposed: ProposedWorkflow): ServiceResponse[Workflow] =
    database.insertWorkflow(proposed)

  def getWorkflows: ServiceResponse[Vector[Workflow]] =
    database.getAllWorkflows

}

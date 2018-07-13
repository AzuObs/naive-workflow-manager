package com.naiveworkflow.app.services

import com.naiveworkflow.app.types.ServiceResponse
import com.naiveworkflow.app.models.{ProposedWorkflow, Workflow}
import com.naiveworkflow.app.database.WorkflowDAOInterface

trait WorkflowServiceInterface {

  protected val database: WorkflowDAOInterface

  def createWorkflow(proposed: ProposedWorkflow): ServiceResponse[Workflow]

  def getWorkflows: ServiceResponse[Vector[Workflow]]

}

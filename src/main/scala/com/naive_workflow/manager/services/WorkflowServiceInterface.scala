package com.naive_workflow.manager.services

import scala.concurrent.Future

import com.naive_workflow.manager.types.ServiceResponse
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}
import com.naive_workflow.manager.database.WorkflowDAOInterface

trait WorkflowServiceInterface {

  protected def database: WorkflowDAOInterface

  def createWorkflow(proposed: ProposedWorkflow): ServiceResponse[Workflow]

  def getWorkflows: ServiceResponse[Vector[Workflow]]

}

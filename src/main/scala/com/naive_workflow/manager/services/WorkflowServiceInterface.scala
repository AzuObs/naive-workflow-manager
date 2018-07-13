package com.naive_workflow.manager.services

import scala.concurrent.Future

import com.naive_workflow.manager.models.{Workflow, Workflows}
import com.naive_workflow.manager.database.WorkflowDAOInterface

trait WorkflowServiceInterface {

  protected def database: WorkflowDAOInterface

  def createWorkflow(nSteps: Int): Workflow
  def getWorkflows: Future[Workflows]

}
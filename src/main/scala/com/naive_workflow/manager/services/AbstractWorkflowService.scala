package com.naive_workflow.manager.services

import scala.concurrent.Future
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

trait AbstractWorkflowService extends WorkflowServiceInterface {

  def createWorkflow(proposed: ProposedWorkflow): Future[Workflow] =
    database.insertWorkflow(proposed)

  def getWorkflows: Future[Vector[Workflow]] =
    database.getAllWorkflows

}

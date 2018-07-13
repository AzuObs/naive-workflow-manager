package com.naive_workflow.manager.services

import scala.concurrent.Future

import com.naive_workflow.manager.models.Workflow

trait AbstractWorkflowService extends WorkflowServiceInterface {

  def createWorkflow(nSteps: Int): Workflow = ???

  def getWorkflows: Future[Vector[Workflow]] =
    database.getAllWorkflows

}
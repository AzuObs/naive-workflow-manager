package com.naive_workflow.manager.services

import scala.concurrent.Future
import com.naive_workflow.manager.database.WorkflowDAOInterface
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

case class WorkflowService(db: WorkflowDAOInterface) extends WorkflowServiceInterface {

  def database: WorkflowDAOInterface = db

  def createWorkflow(proposed: ProposedWorkflow): Future[Workflow] =
    database.insertWorkflow(proposed)

  def getWorkflows: Future[Vector[Workflow]] =
    database.getAllWorkflows

}

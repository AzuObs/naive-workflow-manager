package com.naive_workflow.manager.database

import scala.concurrent.Future
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

trait WorkflowDAOInterface {

  def getAllWorkflows: Future[Vector[Workflow]]

  def insertWorkflow(proposed: ProposedWorkflow): Future[Workflow]

}

package com.naive_workflow.manager.database

import scala.concurrent.Future

import com.naive_workflow.manager.models.{Workflow, Workflows}
import com.naive_workflow.IO

trait WorkflowDAOInterface {

  def insertWorkflow(nSteps: Int): IO[Workflow]

  def getAllWorkflows: Future[Workflows]

}

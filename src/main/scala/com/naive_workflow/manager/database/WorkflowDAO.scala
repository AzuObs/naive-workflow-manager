package com.naive_workflow.manager.database

import com.naive_workflow.IO
import com.naive_workflow.manager.models.Workflow

import scala.concurrent.Future

object WorkflowDAO extends WorkflowDAOInterface {

  def insertWorkflow(nSteps: Int): IO[Workflow] = ???

  def getAllWorkflows: Future[Vector[Workflow]] = ???

}
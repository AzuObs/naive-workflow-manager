package com.naive_workflow.manager.services

import scala.util.{Failure,Success} // daniel
import scala.concurrent.Future

import com.naive_workflow.manager.models.{Workflow, Workflows}

trait AbstractWorkflowService extends WorkflowServiceInterface {

  def createWorkflow(nSteps: Int): Workflow = ???

  import scala.concurrent.ExecutionContext.Implicits.global // daniel
  def getWorkflows: Future[Workflows] =
    database
      .getAllWorkflows
      .transform {
        case Success(v) =>
          println(s"Service: $v \n")
          Success(v)
        case Failure(e) =>
          Failure(e)
      }

}
package com.naive_workflow.manager.database

import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._

import com.naive_workflow.IO
import com.naive_workflow.manager.models.{Workflow, Workflows}

// daniel queryed implicit?
class WorkflowDAO()(implicit ec: ExecutionContext) extends WorkflowDAOInterface {

  def insertWorkflow(nSteps: Int): IO[Workflow] = ???

  def getAllWorkflows: Future[Workflows] =
    Future {
      // daniel
      val w = DB.readOnly { implicit session => {
        sql"""
             | SELECT
             |   workflow_id AS workflowId,
             |   n_steps AS nSteps
             | FROM
             |   workflows
             | WHERE
             |   deleted_at IS NOT NULL
        """.stripMargin
          .map(toWorkflow).list.apply() // daniel
        }.foldLeft(Workflows(Vector.empty[Workflow]))(toWorkflows)
      }

      println(s"DAO: $w\n")
      w // daniel
    }

  private def toWorkflow(rs: WrappedResultSet): Workflow =
    Workflow(
      workflowId = rs.int("workflowId"),
      nSteps = rs.int("nSteps")
    )

  private def toWorkflows(w: Workflows, workflow: Workflow): Workflows =
    Workflows(
      workflows = w.workflows :+ workflow
    )
}
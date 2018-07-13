package com.naive_workflow.manager.database

import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

// daniel curried implicit?
class WorkflowDAO()(implicit ec: ExecutionContext) extends WorkflowDAOInterface {

  def getAllWorkflows: Future[Vector[Workflow]] =
    Future {
      DB.readOnly { implicit session => {
        sql"""
          | SELECT
          |   `workflow_id` AS workflowId,
          |   `n_steps` AS nSteps
          | FROM
          |   workflows
          | WHERE
          |   `deleted_at` IS NOT NULL
        """
          .stripMargin
          .map(toWorkflow).list.apply() // daniel
        }.toVector
      }
    }

  def insertWorkflow(proposed: ProposedWorkflow): Future[Workflow] =
    Future {
      // daniel State[Int] ??
      var newId: Long = 0

      DB.localTx { implicit session =>
        newId = sql"""
          | INSERT INTO
          |   workflows(`n_steps`)
          | VALUES
          |   (${proposed.nSteps})
        """
          .stripMargin
          .updateAndReturnGeneratedKey()
          .apply()
      }

      DB.readOnly { implicit session =>
        sql"""
             | SELECT
             |   `workflow_id` AS workflowId,
             |   `n_steps` AS nSteps
             | FROM
             |   workflows
             | WHERE
             |   `workflow_id` = $newId
        """
          .stripMargin
          .map(toWorkflow)
          .single()
          .apply()
      } match {
        case Some(workflow) => workflow
        case _ => Workflow(0, 0) // daniel
      }
    }

  private def toWorkflow(rs: WrappedResultSet): Workflow =
    Workflow(
      workflowId = rs.int("workflowId"),
      nSteps = rs.int("nSteps")
    )
}
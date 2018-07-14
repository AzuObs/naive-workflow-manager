package com.naive_workflow.manager.database

import scalikejdbc._
import scala.concurrent.{ExecutionContext, Future}

import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

case class WorkflowDAO(implicit ec: ExecutionContext) extends WorkflowDAOInterface {
  def getAllWorkflows: Future[Vector[Workflow]] =
    Future {
      DB.readOnly { implicit session => {
        sql"""
          | SELECT
          |   `workflow_id` AS workflowId,
          |   `n_steps` AS nSteps,
          |   `created_at` AS createdAt,
          |   `updated_at` AS updatedAt
          | FROM
          |   workflows
        """
          .stripMargin
          .map(toWorkflow)
          .list
          .apply()
        }.toVector
      }
    }

  // daniel try catch finally?
  def insertWorkflow(proposed: ProposedWorkflow): Future[Workflow] =
    Future {
      val newId: Long =
        DB.localTx { implicit session =>
          sql"""
            | INSERT INTO
            |   workflows(`n_steps`)
            | VALUES
            |   (${proposed.nSteps});
          """
            .stripMargin
            .updateAndReturnGeneratedKey
            .apply()
        }

      DB.readOnly { implicit session =>
        sql"""
          |SELECT
          |   `workflow_id` AS workflowId,
          |   `n_steps` AS nSteps,
          |   `created_at` AS createdAt,
          |   `updated_at` AS updatedAt
          | FROM
          |   workflows
          | WHERE
          |   `workflow_id` =  $newId
        """
          .stripMargin
          .map(toWorkflow)
          .single()
          .apply()
      } match {
          // daniel ?
        case Some(workflow) => workflow
      }
    }

  private def toWorkflow(rs: WrappedResultSet): Workflow =
    Workflow(
      workflowId = rs.int("workflowId"),
      nSteps = rs.int("nSteps"),
      createdAt = rs.string("createdAt"),
      updatedAt = rs.string("updatedAt")
    )

}

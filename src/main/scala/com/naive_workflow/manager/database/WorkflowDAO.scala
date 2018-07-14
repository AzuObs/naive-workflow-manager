package com.naive_workflow.manager.database

import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}
import com.naive_workflow.manager.types.DAOResponse
import com.naive_workflow.manager.models._

case class WorkflowDAO(implicit ec: ExecutionContext) extends WorkflowDAOInterface {

  def getAllWorkflows: DAOResponse[Vector[Workflow]] =
    Future {
      DB.readOnly { implicit session =>
        try {
          val workflows: Vector[Workflow] = sql"""
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
            .toVector

          Right(workflows)
        }
        catch {
          case _: Exception => Left(DatabaseUnexpected())
        }
      }
    }

  def insertWorkflow(proposed: ProposedWorkflow): DAOResponse[Workflow] =
    Future {
      try {
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
          case Some(workflow) => Right(workflow)
          case None => Left(DatabaseResourceNotFound())
        }
      }
      catch {
        case _: Exception => Left(DatabaseUnexpected())
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

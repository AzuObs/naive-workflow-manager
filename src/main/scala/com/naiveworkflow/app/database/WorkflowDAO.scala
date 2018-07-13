package com.naiveworkflow.app.database

import scalikejdbc._
import scala.concurrent.{ExecutionContext, Future}
import com.naiveworkflow.app.types.DAOResponse
import com.naiveworkflow.app.models._

case class WorkflowDAO(implicit ec: ExecutionContext) extends WorkflowDAOInterface {

  def getAllWorkflows: DAOResponse[Vector[Workflow]] =
    Future {
      DB.readOnly { implicit session =>
        try {
          Right {
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
              .toVector
          }}
        catch {
          case _: Exception => Left(DatabaseUnexpected())
        }}}

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
        }}
      catch {
        case _: Exception => Left(DatabaseUnexpected())
      }}

  private def toWorkflow(rs: WrappedResultSet): Workflow =
    Workflow(
      workflowId = rs.int("workflowId"),
      nSteps = rs.int("nSteps"),
      createdAt = rs.string("createdAt"),
      updatedAt = rs.string("updatedAt")
    )

}

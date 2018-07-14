package com.naiveworkflow.app.database

import scala.collection.immutable.Vector
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._
import com.naiveworkflow.app.types.DAOResponse
import com.naiveworkflow.app.models._
import com.mysql.jdbc.exceptions.jdbc4.{
  MySQLIntegrityConstraintViolationException => ConstraintViolationException
}

case class WorkflowExecutionDAO(implicit ec: ExecutionContext)
  extends WorkflowExecutionDAOInterface {

  def getWorkflowExecutions(workflowId: Int): DAOResponse[Vector[WorkflowExecution]] =
    Future {
      try {
        DB.readOnly { implicit session =>
          sql"""
            | SELECT
            |   `workflow_id` AS workflowId
            | FROM
            |   workflows
            | WHERE
            |   `workflow_id` = $workflowId
            """
            .stripMargin
            .map(_.int("workflowId"))
            .single()
            .apply()
        } match {
          case None => Left(DatabaseResourceNotFound())
          case Some(_) =>
            DB.readOnly { implicit session =>
              val executions =
                sql"""
                  | SELECT
                  |   we.`workflow_execution_id` AS workflowExecutionId,
                  |   we.`workflow_id` AS workflowId,
                  |   we.`current_step_index` AS  currentStepIndex,
                  |   we.`created_at` AS createdAt,
                  |   we.`updated_at` AS updatedAt
                  | FROM
                  |   workflows w
                  |   INNER JOIN workflow_executions we ON w.`workflow_id` = we.`workflow_id`
                  | WHERE
                  |   w.`workflow_id` = $workflowId
                  """
                  .stripMargin
                  .map(toWorkflowExecution)
                  .list
                  .apply()
                  .toVector

              Right(executions)
            }}
      }
      catch {
        case _: Exception => Left(DatabaseUnexpected())
      }}

  def getTerminatedWorkflowExecutions: DAOResponse[Vector[WorkflowExecution]] =
    Future {
      try {
        DB.readOnly { implicit session =>
          val executions =
            sql"""
                 |SELECT
                 |   we.`workflow_execution_id` AS workflowExecutionId,
                 |   we.`workflow_id` AS workflowId,
                 |   we.`current_step_index` AS  currentStepIndex,
                 |   we.`created_at` AS createdAt,
                 |   we.`updated_at` AS updatedAt
                 | FROM
                 |   workflows w
                 |   INNER JOIN workflow_executions we ON w.`workflow_id` = we.`workflow_id`
                 | WHERE
                 |   w.`n_steps` = we.`current_step_index`
              """
              .stripMargin
              .map(toWorkflowExecution)
              .list
              .apply()
              .toVector

          Right(executions)
        }
      }
      catch {
        case _: Exception => Left(DatabaseUnexpected())
      }}

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution): DAOResponse[WorkflowExecution] =
    Future {
      try {
        val newId =
          DB.localTx { implicit session =>
            sql"""
                 | INSERT INTO
                 |   workflow_executions(`workflow_id`, `current_step_index`)
                 | VALUES
                 |   (${proposed.workflowId}, 0);
              """
              .stripMargin
              .updateAndReturnGeneratedKey
              .apply()
          }

        DB.readOnly { implicit session =>
          sql"""
               | SELECT
               |   `workflow_execution_id` AS workflowExecutionId,
               |   `workflow_id` AS workflowId,
               |   `current_step_index` AS currentStepIndex,
               |   `created_at` AS createdAt,
               |   `updated_at` AS updatedAt
               | FROM
               |   workflow_executions
               | WHERE
               |   `workflow_execution_id` = $newId
            """
            .stripMargin
            .map(toWorkflowExecution)
            .single()
            .apply()
        } match {
          case Some(workflowExecution) => Right(workflowExecution)
          case None => Left(DatabaseResourceNotFound())
        }
      }
      catch {
        case _: ConstraintViolationException => Left(DatabaseResourceNotFound())
        case e: Exception => {
          println(e)
          Left(DatabaseUnexpected())
        }}}

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    DAOResponse[WorkflowExecution] =
      Future {
        try {
          val updates: Int =
            DB.localTx { implicit session =>
              sql"""
                   | UPDATE
                   |   workflow_executions we
                   |   INNER JOIN workflows w ON w.`workflow_id` = we.`workflow_id`
                   | SET
                   |   we.`current_step_index` = we.`current_step_index` + 1
                   | WHERE
                   |   we.`current_step_index` < w.`n_steps`
                   |   AND w.`workflow_id` = ${proposed.workflowId}
                   |   AND we.`workflow_execution_id` = ${proposed.workflowExecutionId}
              """
                .stripMargin
                .update
                .apply()
            }

          if (updates == 0) Left(BusinessUnableToIncrementWorkflowExecution())
          else
            DB.readOnly { implicit session =>
              sql"""
                |SELECT
                |   `workflow_execution_id` AS workflowExecutionId,
                |   `workflow_id` AS workflowId,
                |   `current_step_index` AS  currentStepIndex,
                |   `created_at` AS createdAt,
                |   `updated_at` AS updatedAt
                | FROM
                |   workflow_executions
                | WHERE
                |   `workflow_id` = ${proposed.workflowId}
                |   AND `workflow_execution_id` = ${proposed.workflowExecutionId}
                """
                .stripMargin
                .map(toWorkflowExecution)
                .single()
                .apply()
            } match {
              case Some(execution) => Right(execution)
              case None => Left(DatabaseResourceNotFound())
            }
        }
        catch {
          case _: Exception => Left(DatabaseUnexpected())
        }}

  def deleteWorkflowExecutions(workflowExecutions: Vector[WorkflowExecution]):
    DAOResponse[Vector[WorkflowExecution]] =
      Future {
        try {
          DB.localTx { implicit session =>
            val batchValues: Seq[Seq[Any]] =
              workflowExecutions.map(w => Seq[Any](w.workflowExecutionId))

            sql"""
              | DELETE FROM
              |   workflow_executions
              | WHERE
              |   `workflow_execution_id` = ?
              """
              .stripMargin
              .batch(batchValues: _*)
              .apply()

            Right(workflowExecutions)
          }
        }
        catch {
          case _: Exception => Left(DatabaseUnexpected())
        }}

  private def toWorkflowExecution(rs: WrappedResultSet): WorkflowExecution =
    WorkflowExecution(
      workflowExecutionId = rs.int("workflowExecutionId"),
      workflowId = rs.int("workflowId"),
      currentStepIndex = rs.int("currentStepIndex"),
      createdAt = rs.string("createdAt"),
      updatedAt = rs.string("updatedAt")
    )

}

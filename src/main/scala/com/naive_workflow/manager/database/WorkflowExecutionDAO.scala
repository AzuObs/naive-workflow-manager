package com.naive_workflow.manager.database

import scala.collection.immutable.Vector
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._
import com.naive_workflow.manager.types.DAOResponse
import com.naive_workflow.manager.models._

case class WorkflowExecutionDAO(implicit ec: ExecutionContext) extends WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution): DAOResponse[WorkflowExecution] =
    Future {
      // daniel - what if workflow_id doesn't exist?
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
        case _: Exception => Left(DatabaseUnexpected())
      }
    }

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation):
    DAOResponse[WorkflowExecution] =
      Future {
        // daniel will be 0 if it can't update
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

          // daniel isn't this left completely useless?
          if (updates == 0) Left(BusinessUnableToIncrementWorkflowExecution())

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

  // daniel what if number of deletions doesn't match number of ids sent over?
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
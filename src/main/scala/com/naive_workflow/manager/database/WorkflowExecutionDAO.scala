package com.naive_workflow.manager.database

import java.time.ZonedDateTime

import scala.collection.immutable.Vector
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._
import com.naive_workflow.manager.models.{
  ProposedWorkflowExecution,
  ProposedWorkflowExecutionIncrementation,
  WorkflowExecution
}

// daniel createdAt, updatedAt to Workflow?

class WorkflowExecutionDAO()(implicit ec: ExecutionContext)
  extends WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution): Future[WorkflowExecution] =
    Future {
      // daniel - what if workflow is deleted?
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
        // daniel ?
        case Some(workflowExecution) => workflowExecution
        case None => WorkflowExecution(1, 1, 1, "", "") // daniel
      }
    }

  def incrementWorkflowExecution(proposed: ProposedWorkflowExecutionIncrementation): Future[WorkflowExecution] =
    Future {
      // daniel will be 0 if it can't update
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
            |   AND w.`deleted_at` IS NULL
            |   AND we.`workflow_execution_id` = ${proposed.workflowExecutionId}
            |   AND we.`deleted_at` IS NULL
            """
              .stripMargin
              .update
              .apply()
          }

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
        case Some(workflowExecution) => workflowExecution
        case None => WorkflowExecution(1, 1, 1, "", "") // daniel
      }
    }

  // daniel add deleted_at condition even if you can't delete workflows
  // daniel look at all queries and add indexes
  def getTerminatedWorkflowExecutions: Future[Vector[WorkflowExecution]] =
    Future {
      DB.readOnly { implicit session => {
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
          |   AND we.`deleted_at` IS NULL
          """
          .stripMargin
          .map(toWorkflowExecution)
          .list
          .apply()
      }.toVector
    }}

  def deleteWorkflowExecutions(workflowExecutions: Vector[WorkflowExecution]):
    Future[Vector[WorkflowExecution]] =
      Future {
        DB.localTx { implicit session =>
          val now = ZonedDateTime.now()
          val batchValues: Seq[Seq[Any]] =
            workflowExecutions.map(w => Seq[Any](now, w.workflowExecutionId))

          sql"""
            | UPDATE
            |  workflow_executions
            | SET
            |  `deleted_at` = ?
            | WHERE
            |  `workflow_execution_id` = ?
            """
            .stripMargin
            .batch(batchValues: _*)
            .apply()

          workflowExecutions // daniel errors?
        }}

  private def toWorkflowExecution(rs: WrappedResultSet): WorkflowExecution =
    WorkflowExecution(
      workflowExecutionId = rs.int("workflowExecutionId"),
      workflowId = rs.int("workflowId"),
      currentStepIndex = rs.int("currentStepIndex"),
      createdAt = rs.string("createdAt"),
      updatedAt = rs.string("updatedAt")
    )// daniel
}
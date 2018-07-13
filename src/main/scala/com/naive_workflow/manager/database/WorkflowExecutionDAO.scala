package com.naive_workflow.manager.database

import java.time.ZonedDateTime
import scalikejdbc._
import scala.collection.immutable.Vector
import scala.concurrent.{ExecutionContext, Future}

import com.naive_workflow.manager.models.{ProposedWorkflowExecution, WorkflowExecution}

// daniel createdAt, updatedAt to Workflow?

class WorkflowExecutionDAO()(implicit ec: ExecutionContext)
  extends WorkflowExecutionDAOInterface {

  def insertWorkflowExecution(proposed: ProposedWorkflowExecution): Future[WorkflowExecution] =
    Future {
      // daniel - what if workflow is deleted?
      val newId: Long =
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
      }
    }

  def updateWorkflowExecution(workflowExecutionId: Int, currentStepIndex: Int): Future[WorkflowExecution]
    = ???

  // daniel look at all queries and add indexes
  def getTerminatedWorkflowExecutions: Future[Vector[WorkflowExecution]]
    = Future {
    DB.readOnly {
      implicit session => {
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
    Future[Vector[WorkflowExecution]]
      = Future {
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
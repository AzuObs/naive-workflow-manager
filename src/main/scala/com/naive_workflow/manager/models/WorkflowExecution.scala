package com.naive_workflow.manager.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.naive_workflow.Timestamp

final case class WorkflowExecution(
  workflowExecutionId: Int,
  workflowId: Int,
  currentStepIndex: Int,
  createdAt: Timestamp,
  updatedAt: Timestamp
)

final case class WorkflowExecutions(
  workflowExecutions: Vector[WorkflowExecution]
)

final case class ProposedWorkflowExecution(
  workflowId: Int
)

trait WorkflowExecutionJsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val workflowExecutionJsonFormat = jsonFormat5(WorkflowExecution)
  implicit val workflowExecutionsJsonFormat = jsonFormat1(WorkflowExecutions)
  implicit val proposedWorkflowExecutionJsonFormat = jsonFormat1(ProposedWorkflowExecution)
}

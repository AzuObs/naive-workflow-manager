package com.naive_workflow.manager.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.naive_workflow.Datetime

final case class WorkflowExecution(
  workflowExecutionId: Int,
  workflowId: Int,
  currentStepIndex: Int,
  createdAt: Datetime,
  updatedAt: Datetime
)

final case class WorkflowExecutions(
  workflowExecutions: Vector[WorkflowExecution]
)

// daniel enrich this with optional values?
final case class ProposedWorkflowExecution(
  workflowId: Int
)

final case class ProposedWorkflowExecutionIncrementation(
  workflowExecutionId: Int,
  workflowId: Int
)

trait WorkflowExecutionJsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val workflowExecutionJsonFormat =
    jsonFormat5(WorkflowExecution)
  implicit val workflowExecutionsJsonFormat =
    jsonFormat1(WorkflowExecutions)
  implicit val proposedWorkflowExecutionJsonFormat =
    jsonFormat1(ProposedWorkflowExecution)
  implicit val proposedWorkflowExecutionIncrementationJsonFormat =
    jsonFormat2(ProposedWorkflowExecutionIncrementation)

}

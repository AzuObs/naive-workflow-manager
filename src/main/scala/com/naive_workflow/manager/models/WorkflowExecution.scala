package com.naive_workflow.manager.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.naive_workflow.EpochSeconds

final case class WorkflowExecution(
  workflowExecutionId: Int,
  workflowId: Int,
  currentStepIndex: Int,
  createdAt: EpochSeconds
)

trait WorkflowExecutionJsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val workflowExecutionJsonFormat = jsonFormat4(WorkflowExecution)
}

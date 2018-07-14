package com.naive_workflow.manager.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.naive_workflow.Datetime

final case class Workflow (
  workflowId: Int,
  nSteps: Int,
  createdAt: Datetime,
  updatedAt: Datetime
)

final case class Workflows (
  workflows: Vector[Workflow]
)

final case class ProposedWorkflow (
  nSteps: Int
)

trait WorkflowJsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val workflowJsonFormat = jsonFormat4(Workflow)
  implicit val workflowsJsonFormat = jsonFormat1(Workflows)
  implicit val proposedWorkflowJsonFormat = jsonFormat1(ProposedWorkflow)

}

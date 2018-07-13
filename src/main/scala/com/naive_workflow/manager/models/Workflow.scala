package com.naive_workflow.manager.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

final case class Workflow(
  workflowId: Int,
  nSteps: Int
)

final case class Workflows(
  workflows: Vector[Workflow]
)

final case class ProposedWorkflow (
  nSteps: Int
)

trait WorkflowJsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val workflowJsonFormat = jsonFormat2(Workflow)
  implicit val workflowsJsonFormat = jsonFormat1(Workflows)
  implicit val proposedWorkflow = jsonFormat1(ProposedWorkflow)
}

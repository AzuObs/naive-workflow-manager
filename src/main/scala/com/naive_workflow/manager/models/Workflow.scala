package com.naive_workflow.manager.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

final case class Workflow(
  workflowId: Int,
  nSteps: Int
)

trait WorkflowJsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val workflowJsonFormat = jsonFormat2(Workflow)
}

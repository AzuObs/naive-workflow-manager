package com.naiveworkflow.app.actors

import akka.actor.{Actor, Props}
import com.naiveworkflow.app.models.ProposedWorkflow
import com.naiveworkflow.app.services.WorkflowService

object WorkflowActor {

  final case object GetWorkflows
  final case class CreateWorkflow(proposed: ProposedWorkflow)

  def props: Props = Props[WorkflowActor]

}

case class WorkflowActor(service: WorkflowService) extends Actor {
  import WorkflowActor._

  def receive: Receive = {
    case GetWorkflows =>
      sender() ! service.getWorkflows
    case CreateWorkflow(proposed) =>
      sender() ! service.createWorkflow(proposed)
  }

}

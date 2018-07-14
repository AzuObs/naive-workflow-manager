package com.naive_workflow.manager.actors

import akka.actor.{Actor, Props}
import com.naive_workflow.manager.models.ProposedWorkflow
import com.naive_workflow.manager.database.WorkflowDAOInterface
import com.naive_workflow.manager.services.WorkflowService

object WorkflowActor {
  final case object GetWorkflows
  final case class CreateWorkflow(proposed: ProposedWorkflow)

  def props: Props = Props[WorkflowActor]
}

case class WorkflowActor(db: WorkflowDAOInterface) extends Actor {
  import WorkflowActor._

  def receive: Receive = {
    case GetWorkflows =>
      sender() ! WorkflowService(db).getWorkflows
    case CreateWorkflow(proposed) =>
      sender() ! WorkflowService(db).createWorkflow(proposed)
  }

}

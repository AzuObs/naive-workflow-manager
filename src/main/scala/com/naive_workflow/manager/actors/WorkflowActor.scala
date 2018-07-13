package com.naive_workflow.manager.actors

import akka.actor.{Actor, Props}
import com.naive_workflow.manager.database.WorkflowDAOInterface
import com.naive_workflow.manager.models.ProposedWorkflow
import com.naive_workflow.manager.services.WorkflowService

object WorkflowActor {
  final case object GetWorkflows
  final case class CreateWorkflow(proposed: ProposedWorkflow)

  def props: Props = Props[WorkflowActor]
}

// daniel make sure all imports are being used in all files
// daniel implicit ???
// daniel ask pattern?
case class WorkflowActor(db: WorkflowDAOInterface) extends Actor {
  import WorkflowActor._

  def receive: Receive = {
    case GetWorkflows =>
      sender() ! WorkflowService(db).getWorkflows
    case CreateWorkflow(proposed) =>
      sender() ! WorkflowService(db).createWorkflow(proposed)
  }
}

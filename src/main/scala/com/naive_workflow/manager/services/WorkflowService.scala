package com.naive_workflow.manager.services

import com.naive_workflow.manager.database.{WorkflowDAOInterface}

case class WorkflowService(db: WorkflowDAOInterface) extends AbstractWorkflowService {

  def database: WorkflowDAOInterface = db

}
package com.naive_workflow.manager.services

import com.naive_workflow.manager.database.{WorkflowExecutionDAO, WorkflowExecutionDAOInterface}

case class WorkflowExecutionService(db: WorkflowExecutionDAOInterface)
  extends AbstractWorkflowExecutionService {

  // daniel implicit pattern?
  def database: WorkflowExecutionDAOInterface = db

}
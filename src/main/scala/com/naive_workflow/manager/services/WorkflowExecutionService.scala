package com.naive_workflow.manager.services

import com.naive_workflow.manager.database.{WorkflowExecutionDAO, WorkflowExecutionDAOInterface}

object WorkflowExecutionService extends AbstractWorkflowExecutionService {

  def database: WorkflowExecutionDAOInterface = WorkflowExecutionDAO

}
package com.naive_workflow.manager.services

import com.naive_workflow.manager.database.{WorkflowDAO, WorkflowDAOInterface}

object WorkflowService extends AbstractWorkflowService {

  def database: WorkflowDAOInterface = WorkflowDAO

}
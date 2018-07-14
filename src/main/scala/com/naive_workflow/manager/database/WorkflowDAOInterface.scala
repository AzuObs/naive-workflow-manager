package com.naive_workflow.manager.database

import com.naive_workflow.manager.types.DAOResponse
import com.naive_workflow.manager.models.{ProposedWorkflow, Workflow}

trait WorkflowDAOInterface {

  def getAllWorkflows: DAOResponse[Vector[Workflow]]

  def insertWorkflow(proposed: ProposedWorkflow): DAOResponse[Workflow]

}

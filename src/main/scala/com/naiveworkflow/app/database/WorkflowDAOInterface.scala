package com.naiveworkflow.app.database

import com.naiveworkflow.app.types.DAOResponse
import com.naiveworkflow.app.models.{ProposedWorkflow, Workflow}

trait WorkflowDAOInterface {

  def getAllWorkflows: DAOResponse[Vector[Workflow]]

  def insertWorkflow(proposed: ProposedWorkflow): DAOResponse[Workflow]

}

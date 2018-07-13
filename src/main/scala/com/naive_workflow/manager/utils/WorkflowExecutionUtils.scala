package com.naive_workflow.manager.utils

import com.naive_workflow.manager.models.{WorkflowExecution, WorkflowExecutions}

object WorkflowExecutionUtils {
  // daniel make generic?
  def executionsTraversableToExecutionsModel(ws: Traversable[WorkflowExecution]): WorkflowExecutions =
    ws.foldLeft(WorkflowExecutions(Vector()))((z, w) => WorkflowExecutions(z.workflowExecutions :+ w))

}

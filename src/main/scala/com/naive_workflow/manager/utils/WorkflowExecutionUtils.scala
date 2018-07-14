package com.naive_workflow.manager.utils

import com.naive_workflow.manager.models.{
  WorkflowExecution => Execution,
  WorkflowExecutions => Executions
}

object WorkflowExecutionUtils {

  def executionsTraversableToExecutionsModel(ws: Traversable[Execution]): Executions =
    ws.foldLeft(Executions(Vector()))((z, w) => Executions(z.workflowExecutions :+ w))

}

package com.naiveworkflow.app.utils

import com.naiveworkflow.app.models.{
  WorkflowExecution => Execution,
  WorkflowExecutions => Executions
}

object WorkflowExecutionUtils {

  def executionsTraversableToExecutionsModel(ws: Traversable[Execution]): Executions =
    ws.foldLeft(Executions(Vector()))((z, w) => Executions(z.workflowExecutions :+ w))

}

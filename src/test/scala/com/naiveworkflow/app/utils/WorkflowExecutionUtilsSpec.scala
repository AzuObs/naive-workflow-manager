package com.naiveworkflow.app.utils

import org.scalatest._
import com.naiveworkflow.app.Generators._
import com.naiveworkflow.app.models.{WorkflowExecution, WorkflowExecutions}

// daniel candidate for property based testing
class WorkflowExecutionUtilsSpec extends FlatSpec with Matchers {

  "WorkflowExecutionUtils.convertWorkflowsTraversableToWorkflowsModel" should
    "convert Traversable[Workflow] to Workflows" in  {
    val traversable: Vector[WorkflowExecution] =
      genMultipleWorkflowExecution()
    val workflows: WorkflowExecutions =
      WorkflowExecutionUtils.executionsTraversableToExecutionsModel(traversable)

    workflows.workflowExecutions should equal (traversable)
  }

  it should "convert empty Traversable[WorkflowExecution] to empty WorkflowExecutions" in  {
    val traversable: Vector[WorkflowExecution] =
      genMultipleWorkflowExecution(n = 0)
    val workflows: WorkflowExecutions =
      WorkflowExecutionUtils.executionsTraversableToExecutionsModel(traversable)

    workflows.workflowExecutions should equal (Vector.empty[WorkflowExecution])
  }
}

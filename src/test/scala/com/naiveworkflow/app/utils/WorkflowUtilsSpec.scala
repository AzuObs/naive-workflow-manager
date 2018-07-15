package com.naiveworkflow.app.utils

import org.scalatest._
import com.naiveworkflow.app.Generators._
import com.naiveworkflow.app.models.{Workflow, Workflows}

class WorkflowUtilsSpec extends FlatSpec with Matchers {

  "WorkflowUtils.convertWorkflowsTraversableToWorkflowsModel" should
    "convert Traversable[Workflow] to Workflows" in  {
      val traversable: Vector[Workflow] =
        genMultipleWorkflow()
      val workflows: Workflows =
        WorkflowUtils.workflowsTraversableToWorkflowsModel(traversable)

      workflows.workflows should equal (traversable)
    }

    it should "convert empty Traversable[Workflow] to empty Workflows" in  {
      val traversable: Vector[Workflow] =
        genMultipleWorkflow(n = 0)
      val workflows: Workflows =
        WorkflowUtils.workflowsTraversableToWorkflowsModel(traversable)

      workflows.workflows should equal (Vector.empty[Workflow])
    }

}

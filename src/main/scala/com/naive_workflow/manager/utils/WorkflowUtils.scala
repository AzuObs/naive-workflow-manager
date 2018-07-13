package com.naive_workflow.manager.utils

import com.naive_workflow.manager.models.{Workflow, Workflows}

object WorkflowUtils {
  def convertWorkflowsTraversableToWorkflowsModel(workflows: Traversable[Workflow]): Workflows =
    workflows.foldLeft(Workflows(Vector()))((z, w) => Workflows(z.workflows :+ w))
}

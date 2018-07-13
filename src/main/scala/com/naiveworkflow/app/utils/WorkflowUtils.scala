package com.naiveworkflow.app.utils

import com.naiveworkflow.app.models.{Workflow, Workflows}

object WorkflowUtils {

  def workflowsTraversableToWorkflowsModel(workflows: Traversable[Workflow]): Workflows =
    workflows.foldLeft(Workflows(Vector()))((z, w) => Workflows(z.workflows :+ w))

}

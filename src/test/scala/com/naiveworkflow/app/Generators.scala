package com.naiveworkflow.app

import java.sql.Timestamp
import java.util.Date

import scala.math.abs
import scala.util.Random
import com.naiveworkflow.app.types.Datetime
import com.naiveworkflow.app.models._

object Generators {

  def genProposedWorkflow(nSteps: Int = genInt(0, 10)) =
    ProposedWorkflow(
      nSteps
    )

  def genSingleWorkflow(nSteps: Int = genInt(0, 10)) =
    Workflow(
      genId,
      nSteps,
      genDatetime,
      genDatetime
    )

  def genMultipleWorkflow(n: Int = genInt(0, 10)): Vector[Workflow] =
    n match {
      case 0 => Vector()
      case _ => genMultipleWorkflow(n - 1) :+ genSingleWorkflow()
    }

  def genWorkflows(n: Int = genInt(0, 10)) =
    Workflows(genMultipleWorkflow(n))

  def genProposedWorkflowExecution(workflowId: Int = genId) =
    ProposedWorkflowExecution(workflowId)

  def genProposedWorkflowExecutionIncrementation(
    workflowExecutionId: Int = genId,
      workflowId: Int = genId
  ) = ProposedWorkflowExecutionIncrementation(workflowExecutionId, workflowId)

  def genSingleWorkflowExecution(
    workflowExecutionId: Int = genId,
    workflowId: Int = genId,
    currentStep: Int = 0
  ) =
    WorkflowExecution(
      genId,
      workflowId,
      currentStep,
      genDatetime,
      genDatetime
    )

  def genMultipleWorkflowExecution(workflowId: Int = genId, n: Int = genInt(0, 10)):
    Vector[WorkflowExecution] =
      n match {
        case 0 => Vector.empty[WorkflowExecution]
        case _ =>
          genMultipleWorkflowExecution(n - 1, workflowId) :+ genSingleWorkflowExecution(workflowId)
      }

  def genWorkflowExecutions(workflowId: Int = genId, n: Int = genInt(0, 10)) =
    WorkflowExecutions(genMultipleWorkflowExecution(workflowId, n))

  def genDatabaseUnexpectedException =
    DatabaseUnexpected()

  def genInt(min: Int, max: Int): Int = {
    assert(min < max)

    val rand = new Random()
    val positive = abs(rand.nextInt - 1)
    val withinRange = positive % (max - min)

    withinRange + min
  }
  
  def genId: Int =
    genInt(1, Int.MaxValue)
  
  def genDatetime: Datetime =
    new Timestamp(new Date().getTime).toString

  def genUUID: String =
    java.util.UUID.randomUUID.toString
}

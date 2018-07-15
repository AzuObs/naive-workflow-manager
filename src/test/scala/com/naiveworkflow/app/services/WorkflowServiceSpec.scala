package com.naiveworkflow.app.services

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import com.naiveworkflow.app.database.WorkflowDAO
import com.naiveworkflow.app.Fixtures._

class WorkflowServiceSpec
  extends FlatSpec
    with Matchers
    with MockFactory {

  "WorkflowService.getWorkflows" should
    "calls and returns WorkflowDAO.getAllWorkflows" in  {
      val db = mock[WorkflowDAO]
      val service = WorkflowService(db)
      val eitherFuture = Future { Right(genMultipleWorkflow()) }

      db.getAllWorkflows _ expects() returns eitherFuture

      service.getWorkflows should equal(eitherFuture)
     }

  "WorkflowService.createWorkflow" should
    "returns a nSteps too small exception" in  {
      val service = WorkflowService(WorkflowDAO())
      val proposed = genProposedWorkflow(nSteps = 0)

      Await.result(service.createWorkflow(proposed), 5.seconds) match {
        case Left(e) => e should equal(genBusinessStepsTooSmallException)
        case Right(_) => new AssertionError("Oops, that ain't Right!")
      }}

  it should
    "returns an nSteps too big exception" in {
      val service = WorkflowService(WorkflowDAO())
      val proposed = genProposedWorkflow(nSteps = 1001)

      Await.result(service.createWorkflow(proposed), 5.seconds) match {
        case Left(e) => e should equal(genBusinessStepsTooBigException)
        case Right(_) => new AssertionError("Oops, that ain't Right!")
      }}

  it should
    "calls and returns WorkflowDAO.createWorkflow" in  {
      val db = mock[WorkflowDAO]
      val service = WorkflowService(db)
      val proposed = genProposedWorkflow()
      val eitherFuture = Future { Right(genSingleWorkflow()) }

      db.insertWorkflow _ expects proposed returns eitherFuture

      service.createWorkflow(proposed) should equal(eitherFuture)
    }

}

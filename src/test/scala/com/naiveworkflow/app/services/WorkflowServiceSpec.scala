package com.naiveworkflow.app.services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import com.naiveworkflow.app.database.WorkflowDAO
import com.naiveworkflow.app.Generators._

class WorkflowServiceSpec
  extends FlatSpec
    with Matchers
    with MockFactory {

  "WorkflowService.getWorkflows" should
    "calls WorkflowDAO.getAllWorkflows" in  {
      val db = mock[WorkflowDAO]
      val service = WorkflowService(db)

      db.getAllWorkflows _ expects()

      service.getWorkflows
     }

  it should
    "returns database workflows" in  {
      val db = stub[WorkflowDAO]
      val service = WorkflowService(db)
      val eitherFuture = Future { Right(genMultipleWorkflow()) }

      db.getAllWorkflows _ when() returns eitherFuture

      service.getWorkflows should equal(eitherFuture)
   }

  it should
    "returns database exception" in  {
    val db = stub[WorkflowDAO]
    val service = WorkflowService(db)
    val eitherFuture = Future { Left(genDatabaseUnexpectedException) }

    db.getAllWorkflows _ when() returns eitherFuture

    service.getWorkflows should equal(eitherFuture)
  }

  "WorkflowService.createWorkflow" should
    "calls WorkflowDAO" in  {
      val db = mock[WorkflowDAO]
      val service = WorkflowService(db)
      val proposed = genProposedWorkflow()

      db.insertWorkflow _ expects proposed

      service.createWorkflow(proposed)
    }

  it should
    "returns the created database workflow" in  {
      val db = stub[WorkflowDAO]
      val service = WorkflowService(db)
      val proposed = genProposedWorkflow()
      val eitherFuture = Future { Right(genSingleWorkflow()) }

      db.insertWorkflow _ when proposed returns eitherFuture

      service.createWorkflow(proposed) should equal(eitherFuture)
    }

  it should
    "returns database exception" in  {
      val db = stub[WorkflowDAO]
      val service = WorkflowService(db)
      val proposed = genProposedWorkflow()
      val eitherFuture = Future { Left(genDatabaseUnexpectedException) }

      db.insertWorkflow _ when proposed returns eitherFuture

      service.createWorkflow(proposed) should equal(eitherFuture)
    }

}

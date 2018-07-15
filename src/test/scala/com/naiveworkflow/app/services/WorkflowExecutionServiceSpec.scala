package com.naiveworkflow.app.services

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import com.naiveworkflow.app.Fixtures._
import com.naiveworkflow.app.database.WorkflowExecutionDAO

class WorkflowExecutionServiceSpec
  extends FlatSpec
    with Matchers
    with MockFactory {

  "WorkflowExecutionService.getWorkflowExecutions" should
    "calls database.getWorkflowExecutions" in {
      val db = mock[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val workflowId = genId

      db.getAllWorkflowExecutions _ expects workflowId

      service.getWorkflowExecutions(workflowId)
    }

  it should
    "returns database workflow executions" in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val workflowId = genId
      val eitherFuture = Future {
        Right(genMultipleWorkflowExecution(workflowId = workflowId))
      }

      db.getAllWorkflowExecutions _ when workflowId returns eitherFuture

      service.getWorkflowExecutions(workflowId) should equal(eitherFuture)
    }

  it should
    "returns database exception" in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val workflowId = genId
      val eitherFuture = Future {
        Left(genDatabaseUnexpectedException)
      }

      db.getAllWorkflowExecutions _ when workflowId returns eitherFuture

      service.getWorkflowExecutions(workflowId) should equal(eitherFuture)
    }

  "WorkflowExecutionService.createWorkflowExecution" should
    "calls database.insertWorkflowExecution" in {
      val db = mock[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val proposed = genProposedWorkflowExecution()

      db.insertWorkflowExecution _ expects proposed

      service.createWorkflowExecution(proposed)
    }

  it should
    "returns database result " in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val proposed = genProposedWorkflowExecution()
      val eitherFuture = Future {
        Right(genSingleWorkflowExecution(workflowId = proposed.workflowId))
      }

      db.insertWorkflowExecution _ when proposed returns eitherFuture

      service.createWorkflowExecution(proposed) should equal(eitherFuture)
    }

  it should
    "returns database exception" in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val proposed = genProposedWorkflowExecution()
      val eitherFuture = Future {
        Left(genDatabaseUnexpectedException)
      }

      db.insertWorkflowExecution _ when proposed returns eitherFuture

      service.createWorkflowExecution(proposed) should equal(eitherFuture)
    }

  "WorkflowExecutionService.incrementWorkflowExecution" should
    "calls db.incrementWorkflowExecution" in {
      val db = mock[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val proposed = genProposedWorkflowExecutionIncrementation()

      db.incrementWorkflowExecution _ expects proposed

      service.incrementWorkflowExecution(proposed)
    }

  it should
    "returns database result " in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val proposed = genProposedWorkflowExecutionIncrementation()
      val eitherFuture = Future {
        Right(
          genSingleWorkflowExecution(
            workflowExecutionId = proposed.workflowExecutionId, workflowId = proposed.workflowId
          ))}

      db.incrementWorkflowExecution _ when proposed returns eitherFuture

      service.incrementWorkflowExecution(proposed) should equal(eitherFuture)
    }

  it should
    "returns database exception" in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val proposed = genProposedWorkflowExecutionIncrementation()
      val eitherFuture = Future { Left(genDatabaseUnexpectedException) }

      db.incrementWorkflowExecution _ when proposed returns eitherFuture

      service.incrementWorkflowExecution(proposed) should equal(eitherFuture)
    }

  "WorkflowExecutionService.deletedEndedWorkflowExecutions" should
    "calls db.getTerminatedWorkflowExecutions followed by db.deleteWorkflowExecutions in happy path" in {
      val db = mock[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val executions = genMultipleWorkflowExecution(n = 0)

      db.getTerminatedWorkflowExecutions _ expects() returns Future { Right(executions) }
      db.deleteWorkflowExecutions _ expects executions returns Future { Right(executions) }

      Await.result(service.deletedEndedWorkflowExecutions, 5.seconds)
    }

  it should
    "calls db.getTerminatedWorkflowExecutions but not db.deleteWorkflowExecutions if exception" in {
      val db = mock[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val executions = genMultipleWorkflowExecution()
      val getFuture = Future { Left(genDatabaseUnexpectedException) }

      db.getTerminatedWorkflowExecutions _ expects() returns getFuture
      db.deleteWorkflowExecutions _ expects executions never()

      service.deletedEndedWorkflowExecutions
    }

  it should
    "short-circuits and returns database.getTerminatedWorkflowExecutions exception" in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val eitherFuture = Future { Left(genDatabaseUnexpectedException) }

      db.getTerminatedWorkflowExecutions _ when() returns eitherFuture

      service.deletedEndedWorkflowExecutions map {
        case Left(e) => e should equal(genDatabaseUnexpectedException)
        case Right(_) => throw new AssertionError("shouldn't be Right")
      }
    }

  it should
    "returns database.deleteWorkflowExecutions exceptions" in {
      val db = stub[WorkflowExecutionDAO]
      val service = WorkflowExecutionService(db)
      val executions = genMultipleWorkflowExecution()
      val getFuture = Future { Right(executions) }
      val delFuture = Future { Left(genDatabaseUnexpectedException) }

      db.getTerminatedWorkflowExecutions _ when() returns getFuture
      db.deleteWorkflowExecutions _ when executions returns delFuture

      service.deletedEndedWorkflowExecutions map {
        case Left(e) => e should equal(genDatabaseUnexpectedException)
        case Right(_) => throw new AssertionError("shouldn't be Right")
      }
    }

}

package com.naive_workflow.manager.models

import akka.http.scaladsl.model.{StatusCodes, StatusCode} // daniel error handling

sealed trait ApiException {
  val message: String
  val httpStatus: StatusCode
}

final case class DatabaseUnexpected() extends ApiException {
  val message = "InternalServerError: database error"
  val httpStatus = StatusCodes.InternalServerError
}

final case class BusinessUnexpected() extends ApiException {
  val message = "InternalServerError: business error"
  val httpStatus = StatusCodes.InternalServerError
}

final case class DatabaseResourceNotFound() extends ApiException {
  val message = "NotFound: requested resource doesn't exist"
  val httpStatus = StatusCodes.NotFound
}

final case class BusinessUnableToIncrementWorkflowExecution() extends ApiException {
  val message = "Forbidden: unable to increment workflow execution"
  val httpStatus = StatusCodes.Forbidden
}

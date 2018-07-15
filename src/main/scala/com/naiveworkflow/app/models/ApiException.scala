package com.naiveworkflow.app.models

import akka.http.scaladsl.model.{StatusCodes, StatusCode}

sealed trait ApiException {
  val message: String
  val httpStatus: StatusCode
}

final case class DatabaseUnexpected() extends ApiException {
  val message: String = "InternalServerError: database error"
  val httpStatus: StatusCode = StatusCodes.InternalServerError
}

final case class DatabaseResourceNotFound() extends ApiException {
  val message: String = "NotFound: requested resource doesn't exist"
  val httpStatus: StatusCode = StatusCodes.NotFound
}

final case class BusinessUnexpected() extends ApiException {
  val message: String = "InternalServerError: business error"
  val httpStatus: StatusCode = StatusCodes.InternalServerError
}

final case class BusinessStepsTooSmall() extends ApiException {
  val message: String = "Forbidden: Steps must be greater than 0"
  val httpStatus: StatusCode = StatusCodes.Forbidden
}

final case class BusinessStepsTooBig() extends ApiException {
  val message: String = "Forbidden: Steps must be less than 1000"
  val httpStatus: StatusCode = StatusCodes.Forbidden
}

final case class BusinessUnableToIncrementWorkflowExecution() extends ApiException {
  val message: String = "Forbidden: unable to increment workflow execution"
  val httpStatus: StatusCode = StatusCodes.Forbidden
}

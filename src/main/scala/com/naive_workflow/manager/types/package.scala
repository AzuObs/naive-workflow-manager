package com.naive_workflow.manager

import scala.concurrent.Future
import com.naive_workflow.manager.models.ApiException

import scala.util.Either

package object types {

  // Explicitize that the String is a Datetime
  type Datetime = String

  // Explicitize that there the type has contains side effects
  // eg. IO type in Haskell is compulsory for indicating side effects, which is nice
  type IO[E, T] = Either[E, T]

  type DAOResponse[T] = Future[IO[ApiException, T]]

  type ServiceResponse[T] = Future[Either[ApiException, T]]

}

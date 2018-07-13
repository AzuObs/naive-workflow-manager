package com.naiveworkflow.app

import scala.concurrent.Future
import com.naiveworkflow.app.models.ApiException

import scala.util.Either

package object types {

  // Explicitize that the String is a Datetime
  type Datetime = String

  // Explicitize that there the type has contains side effects
  // eg. IO type in Haskell is compulsory for indicating side effects, which is nice
  type IO[T] = Either[ApiException, T]

  type DAOResponse[T] = Future[IO[T]]

  type ServiceResponse[T] = Future[Either[ApiException, T]]

}

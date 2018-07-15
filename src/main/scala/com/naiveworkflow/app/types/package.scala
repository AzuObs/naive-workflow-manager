package com.naiveworkflow.app

import scala.concurrent.Future
import com.naiveworkflow.app.models.ApiException

import scala.util.Either

package object types {

  // Explicitize that the String is a Datetime
  type Datetime = String

  // daniel type Id = Int ??

  // Explicitize that there the type has contains side effects
  // eg. IO type in Haskell is compulsory for indicating side effects, which is nice
  type IO[E, T] = Either[E, T]

  type DAOResponse[T] = Future[IO[ApiException, T]]

  type ServiceResponse[T] = Future[Either[ApiException, T]]

}

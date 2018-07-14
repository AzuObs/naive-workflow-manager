package com.naive_workflow.manager

import scala.concurrent.Future

import com.naive_workflow.IO
import com.naive_workflow.manager.models.ApiException

package object types {

  // daniel bring all types here?
  type DAOResponse[T] = Future[IO[ApiException, T]]
  type ServiceResponse[T] = Future[Either[ApiException, T]]

}

package com

import scala.util.Try

package object naive_workflow {

  type Datetime = String
  type IO[T] = Try[T] // daniel using Futures instead?

}

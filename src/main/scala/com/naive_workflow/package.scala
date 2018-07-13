package com

import scala.util.Try

package object naive_workflow {

  type EpochSeconds = Long
  type IO[T] = Try[T] // daniel using Futures instead?

}

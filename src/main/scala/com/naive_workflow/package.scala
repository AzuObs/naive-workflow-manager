package com

import scala.util.Try

package object naive_workflow {
// daniel none of these are being used
  type Timestamp = String
  type IO[T] = Try[T] // daniel using Futures instead?

}

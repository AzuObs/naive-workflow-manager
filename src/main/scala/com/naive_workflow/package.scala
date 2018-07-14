package com

import scala.util.Either

package object naive_workflow {

  // Explicitize that the String is a Datetime
  type Datetime = String
  // daniel function toDateTime(s: String): Datetime, in com.naive_workflow.utils ??

  // Explicitize that there the type has contains side effects
  // eg. IO type in Haskell is compulsory for indicating side effects, which is nice
  type IO[E, T] = Either[E, T]

}

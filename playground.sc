// daniel delete me
import scala.util._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val a: Either[String, Int] = {
  try {
    Right(10)
  }
  catch {
    case _: Exception => Left("foo")
  }
}




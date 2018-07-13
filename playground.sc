import scala.util.{Success, Failure}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val a: Future[String] =
  Future("foobar")
    .transform {
      case Success(v) => Success(v + " haha")
      case Failure(e) => Failure(e)
    }

a.onComplete {
  case Success(v) => s"you see: $v"
  case Failure(e) => println(s"you don't see $e")
}
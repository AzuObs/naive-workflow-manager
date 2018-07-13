import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


def failAgain: Future[Future[String]] = Future {
  def fail: Future[Int] = Future.failed(new Exception("foobar"))

  val b: Future[String] = fail.transform {
    case Success(v) => Success("Foo")
    case Failure(e) => Success("Bar")
  }

  b
}

//failAgain.onComplete {
//  case Success(v) => println(v)
//  case Failure(e) => println(e)
//}
//
//}





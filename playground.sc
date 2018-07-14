import scala.util.{Success, Failure}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

def long(): Future[Int] = Future {
  Thread.sleep(500)

  69
}

def chain(): Future[Future[Future[String]]] = Future {
  Thread.sleep(1)

  Future {
    Thread.sleep(1)

    Future {
      Thread.sleep(1)

      "hello"
    }
  }
}

val dd: Future[String] = for {
  a: Future[Future[String]] <- chain()
  b: Future[String] <- a
  c: String <- b
} yield c

dd onComplete {
  case Success(m) => println(m)
  case Failure(f) => println(f)
}

Await.result(long(), 10.seconds)
Await.result(long(), 10.seconds)
Await.result(long(), 10.seconds)
Await.result(long(), 10.seconds)
Await.result(long(), 10.seconds)


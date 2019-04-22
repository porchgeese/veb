package porchgeese.veb

import java.util.concurrent.Executors

import cats.effect.{ContextShift, IO, Resource, Timer}
import cats.implicits._

import scala.concurrent.ExecutionContext
package object unit {
  def async(testFun: (ExecutionContext, ContextShift[IO], Timer[IO]) => IO[_]): Unit =
    Resource
      .make(IO(Executors.newFixedThreadPool(10)))(ec => IO(ec.shutdown()))
      .map(ExecutionContext.fromExecutor)
      .use { ec =>
        val timer = IO.timer(ec)
        val cs    = IO.contextShift(ec)
        (IO.shift(ec) *> testFun(ec, cs, timer)).void
      }
      .unsafeRunSync()
}

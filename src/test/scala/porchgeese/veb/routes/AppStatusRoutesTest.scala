package porchgeese.veb.routes

import cats.effect.IO
import org.scalatest.{FlatSpec, Matchers}
import porchgeese.veb.unit._
import io.circe.JsonObject
import org.http4s.{Request, Uri}
import org.http4s.dsl.io._
import org.scalactic.TypeCheckedTripleEquals
import porchgeese.veb.health.{HealthCheckService, Unhealthy}
import porchgeese.veb.http._

class AppStatusRoutesTest extends FlatSpec with Matchers with TypeCheckedTripleEquals {

  it should "return a 500 if the service is not healthy" in async { (_, _, _) =>
    val routes = new AppStatusRoutes(stub(Left(Unhealthy(false, false))))
    for {
      request  <- IO.pure(Request[IO](GET, Uri.unsafeFromString("/health")))
      response <- routes.routes.run(request).value
      body     <- response.get.as[JsonObject]
    } yield {
      response.get.status.code shouldBe 500
      body("amqp").get.as[Boolean] should be(Right(false))
      body("db").get.as[Boolean] should be(Right(false))
    }
  }

  it should "return a 200 if the service is healthy" in async { (_, _, _) =>
    val routes = new AppStatusRoutes(stub(Right(())))
    for {
      request  <- IO.pure(Request[IO](GET, Uri.unsafeFromString("/health")))
      response <- routes.routes.run(request).value
    } yield {
      response.get.status.code shouldBe 200
    }
  }

  def stub(result: Either[Unhealthy, Unit]): HealthCheckService = new HealthCheckService {
    override def health: IO[Either[Unhealthy, Unit]] = IO(result)
  }
}

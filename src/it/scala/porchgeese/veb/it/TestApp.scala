package porchgeese.veb.it

import cats.effect.IO
import com.itv.bucky.test.AmqpClientTest
import doobie.util.transactor.Transactor
import io.circe.Json
import org.http4s.client.Client
import porchgeese.veb.app.VebConfig
import porchgeese.veb.http._

trait TestApp {
  def appClient: Client[IO]
  def db: Transactor[IO]
  def amqp: AmqpClientTest[IO]
  def config: VebConfig
  case class HttpException(body: Json) extends Throwable
  def get(url: String) =
    appClient
      .get(url)(r => r.as[Json].map((r.status, _)))
      .flatMap {
        case (s, json) if s.isSuccess => IO(json)
        case (_, json)                => IO.raiseError(HttpException(json))
      }

  def callHealthCheck: IO[Json] = get("/_meta/health")
}

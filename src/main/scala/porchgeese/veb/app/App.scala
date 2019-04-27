package porchgeese.veb.app

import java.time.Clock

import cats.effect.{ContextShift, IO, Timer}
import com.itv.bucky.AmqpClient
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import org.http4s.client.Client
import org.http4s.HttpRoutes

import scala.concurrent.ExecutionContext
import porchgeese.veb.health.HealthCheckService
import porchgeese.veb.routes.{AppStatusRoutes, ProjectRoutes}
import org.http4s.server.Router
import porchgeese.veb.repository.ProjectRepository
import porchgeese.veb.service.{ProjectsService, ServicesService, UUIDService}
import org.http4s.server.middleware._
trait App {
  def ec: ExecutionContext
  def cs: ContextShift[IO]
  def timer: Timer[IO]
  def db: HikariTransactor[IO]
  def client: Client[IO]
  def amqp: AmqpClient[IO]
  def config: VebConfig
  def routes: HttpRoutes[IO]
  def handlers: IO[Unit]
  def migrations: IO[Unit] =
    IO {
      val flyway = new Flyway
      flyway.setDataSource(db.kernel)
      flyway.migrate()
      ()
    }
}

object App {
  def apply(
      _ec: ExecutionContext,
      _cs: ContextShift[IO],
      _timer: Timer[IO],
      _db: HikariTransactor[IO],
      _client: Client[IO],
      _amqp: AmqpClient[IO],
      _config: VebConfig
  ): App = {
    val healthService     = HealthCheckService(_amqp, _db)
    val uuidService       = UUIDService()
    val clock             = Clock.systemUTC()
    val projectRepository = ProjectRepository(_db)
    val projectService    = ProjectsService(projectRepository, uuidService, clock)
    val serviceService    = ServicesService(projectRepository, uuidService, clock)

    val projectRoutes   = new ProjectRoutes(projectService, serviceService)
    val appStatusRoutes = new AppStatusRoutes(healthService)
    val appRoutes = CORS(Router(
      "/_meta"   -> appStatusRoutes.routes,
      "/projects" -> projectRoutes.routes
    ))

    new App {
      override def ec: ExecutionContext     = _ec
      override def cs: ContextShift[IO]     = _cs
      override def timer: Timer[IO]         = _timer
      override def db: HikariTransactor[IO] = _db
      override def client: Client[IO]       = _client
      override def amqp: AmqpClient[IO]     = _amqp
      override def config: VebConfig        = _config
      override def routes: HttpRoutes[IO]   = appRoutes
      override def handlers: IO[Unit]       = IO.unit
    }
  }

}

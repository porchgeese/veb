package porchgeese.veb

import cats.effect.{ExitCode, IO, IOApp, Resource, SyncIO}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext
import cats._
import cats.implicits._
import com.itv.bucky.AmqpClient
import doobie.hikari.HikariTransactor
import porchgeese.veb.app._
import porchgeese.veb.app.{App, VebConfig}
import com.itv.bucky.kamonSupport._
import kamon.Kamon
import kamon.influxdb.InfluxDBReporter
import kamon.system.SystemMetrics

object Main extends IOApp.WithContext {

  def config: SyncIO[VebConfig] =
    SyncIO(
      ConfigFactory.load()
    ).map(c => buildConfig(c).unsafeRunSync)

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] =
    Resource
      .pure[SyncIO, Unit](())
      .evalMap(_ => config)
      .flatMap(mainThreadPool)
      .map(ExecutionContext.fromExecutor(_))

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      config         <- Resource.make(config.to[IO])(_ => IO.unit)
      _              <- Resource.make[IO, Unit](IO(Kamon.loadReportersFromConfig()))(_ => IO.unit)
      _              <- Resource.make(IO(Kamon.addReporter(new InfluxDBReporter())))(r => IO(r.cancel()).void)
      _              <- Resource.make(IO(SystemMetrics.startCollecting()))(_ => IO.unit)
      amqpClient     <- AmqpClient.apply[IO](config.amqp).map(_.withKamonSupport())
      client         <- httpClient(config)(executionContext, contextShift)
      dbConnectionEc <- dbConnectionThreadPool(config).map(ExecutionContext.fromExecutor)
      dbTransactEc   <- dbConnectionThreadPool(config).map(ExecutionContext.fromExecutor)
      transactor <- HikariTransactor
        .newHikariTransactor[IO](config.db.driverClassName, config.db.url, config.db.user, config.db.pass, dbConnectionEc, dbTransactEc)
    } yield {
      App(executionContext, contextShift, timer, transactor, client, amqpClient, config)
    }).use(app => app.handlers *> app.migrations *> server(app).use(_ => IO.never))
}

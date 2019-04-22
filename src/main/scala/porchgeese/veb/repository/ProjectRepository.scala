package porchgeese.veb.repository

import cats.effect.IO
import doobie.util.transactor.Transactor
import porchgeese.veb.model.{Project, Service}
import doobie.implicits._
import porchgeese.veb.repository.query.{ProjectQueries, ServiceQueries}
import cats.implicits._
trait ProjectRepository {
  def create(project: Project): IO[Project]
  def addService(service: Service): IO[Service]
}

object ProjectRepository {
  def apply(db: Transactor[IO]): ProjectRepository = new ProjectRepository() {
    override def create(project: Project): IO[Project] =
      ProjectQueries.insert(project).transact(db) *> IO(project)

    override def addService(service: Service): IO[Service] =
      ServiceQueries.insert(service).transact(db) *> IO(service)

  }
}

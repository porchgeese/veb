package porchgeese.veb.repository

import java.util.UUID

import cats.data.OptionT
import cats.effect.IO
import doobie.util.transactor.Transactor
import porchgeese.veb.model.{Project, ProjectId, ProjectWithServices, Service, ServiceId}
import doobie.implicits._
import porchgeese.veb.repository.query.{ProjectQueries, ServiceQueries}
import cats.implicits._
import cats._

case class ProjectFilter(projectId: Option[UUID])

trait ProjectRepository {
  def findProject(project: ProjectId): IO[Option[ProjectWithServices]]
  def create(project: Project): IO[Project]
  def addService(service: Service): IO[Service]
  def updateService(serviceId: ServiceId)(service: Service): IO[Unit]
  def findService(id: ServiceId): IO[Option[Service]]
}

object ProjectRepository {
  def apply(db: Transactor[IO]): ProjectRepository = new ProjectRepository {
    override def create(project: Project): IO[Project] =
      ProjectQueries.insert(project).transact(db) *> IO(project)

    override def addService(service: Service): IO[Service] =
      ServiceQueries.insert(service).transact(db) *> IO(service)

    override def findProject(id: ProjectId): IO[Option[ProjectWithServices]] =
      (for {
        project  <- ProjectQueries.findProject(id)
        services <- ServiceQueries.findServicesFor(id)
      } yield project.map(ProjectWithServices.from(_, services))).transact(db)

    override def findService(id: ServiceId): IO[Option[Service]] =
      (for {
        service <- ServiceQueries.findService(id)
      } yield service).transact(db)

    override def updateService(serviceId: ServiceId)(service: Service): IO[Unit] =
      (for {
        _ <- ServiceQueries.update(serviceId)(service)
      } yield ()).transact(db)
  }
}

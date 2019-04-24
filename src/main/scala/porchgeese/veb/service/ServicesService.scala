package porchgeese.veb.service

import java.time.{Clock, Instant}

import cats.effect.IO
import porchgeese.veb.model.{Position, ProjectId, Service, ServiceId, ServiceType}
import porchgeese.veb.repository.ProjectRepository
import cats.implicits._

trait ServicesService {
  def create(project: ProjectId, position: Position, name: String, kind: ServiceType): IO[Service]
}

object ServicesService {
  def apply(projectRepository: ProjectRepository, uuidGen: UUIDService, clock: Clock): ServicesService = new ServicesService {
    override def create(project: ProjectId, position: Position, name: String, kind: ServiceType): IO[Service] =
      for {
        now       <- IO(Instant.now(clock))
        serviceId <- uuidGen.generate.map(ServiceId(_))
        service   <- Service(project, position, name, kind, serviceId, now, now).pure[IO]
        result    <- projectRepository.addService(service)
      } yield result
  }
}

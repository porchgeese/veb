package porchgeese.veb.service

import java.time.{Clock, Instant}

import cats.data.OptionT
import cats.effect.IO
import porchgeese.veb.model.{Dimension, Position, ProjectId, Service, ServiceId, ServiceType}
import porchgeese.veb.repository.ProjectRepository
import cats.implicits._
import cats._
import com.typesafe.scalalogging.StrictLogging
import cats.effect.implicits._
import porchgeese.veb.service.ServicesService.OptionResult

trait ServicesService {
  def create(project: ProjectId, position: Position, dimension: Dimension, name: String, kind: ServiceType): IO[Service]
  def updatePosition(position: Position)(service: ServiceId): IO[Option[Service]]
  def findService(service: ServiceId): IO[Option[Service]]
}

object ServicesService {
  type OptionResult[A] = OptionT[IO, A]
  def apply(projectRepository: ProjectRepository, uuidGen: UUIDService, clock: Clock): ServicesService = new ServicesService with StrictLogging {
    override def create(project: ProjectId, position: Position, dimension: Dimension, name: String, kind: ServiceType): IO[Service] =
      for {
        _         <- IO(logger.info(s"Creating new service."))
        now       <- IO(Instant.now(clock))
        serviceId <- uuidGen.generate.map(ServiceId(_))
        _         <- IO(logger.info(s"Generated id: $serviceId"))
        service   <- Service(serviceId, project, position, dimension, name, kind, now, now).pure[IO]
        result    <- projectRepository.addService(service)
        _         <- IO(logger.info(s"Service created."))
      } yield result

    override def findService(serviceId: ServiceId): IO[Option[Service]] = projectRepository.findService(serviceId)

    override def updatePosition(position: Position)(serviceId: ServiceId): IO[Option[Service]] =
      (for {
        _          <- IO(logger.info(s"Updating service $serviceId position to $position.")).to[OptionResult]
        now        <- IO(Instant.now(clock)).to[OptionResult]
        service    <- OptionT(findService(serviceId))
        newService <- service.copy(position = position, updated = now).pure[IO].to[OptionResult]
        _          <- projectRepository.updateService(service.id)(newService).to[OptionResult]
        _          <- IO(logger.info("Service position updated.")).to[OptionResult]
      } yield newService).value
  }
}

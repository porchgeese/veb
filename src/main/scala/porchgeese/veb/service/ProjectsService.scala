package porchgeese.veb.service

import java.time.{Clock, Instant}
import java.util.UUID

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import porchgeese.veb.model.{Project, ProjectId, ProjectName, ProjectWithServices}
import porchgeese.veb.repository.ProjectRepository

trait ProjectsService {
  def create(name: ProjectName): IO[Project]
  def find(projectId: ProjectId): IO[Option[ProjectWithServices]]
}

object ProjectsService {

  def apply(projectRepo: ProjectRepository, uuidService: UUIDService, clock: Clock): ProjectsService = new ProjectsService with StrictLogging {
    override def create(name: ProjectName): IO[Project] =
      for {
        _      <- IO(logger.info("Creating new project."))
        id     <- uuidService.generate.map(ProjectId(_))
        _      <- IO(logger.info(s"Generated id $id."))
        now    <- IO(Instant.now(clock))
        result <- projectRepo.create(Project(id, name, now, now))
        _      <- IO(logger.info("Project created."))
      } yield result

    override def find(projectId: ProjectId): IO[Option[ProjectWithServices]] =
      for {
        _       <- IO(logger.info(s"Querying for project with id $projectId"))
        project <- projectRepo.findProject(projectId)
        _       <- IO(logger.info(s"Found project? : ${project.isDefined}"))
      } yield project
  }
}

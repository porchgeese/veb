package porchgeese.veb.service

import java.time.{Clock, Instant}

import cats.effect.IO
import porchgeese.veb.model.{Project, ProjectId, ProjectName}
import porchgeese.veb.repository.ProjectRepository

trait ProjectsService {
  def create(name: ProjectName): IO[Project]
}

object ProjectsService {
  def apply(projectRepo: ProjectRepository, uuidService: UUIDService, clock: Clock): ProjectsService = new ProjectsService {
    override def create(name: ProjectName): IO[Project] =
      for {
        id     <- uuidService.generate.map(ProjectId(_))
        now    <- IO(Instant.now(clock))
        result <- projectRepo.create(Project(id, name, now, now))
      } yield result
  }
}

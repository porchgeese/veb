package porchgeese.veb.routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import porchgeese.veb.http.Request.{NewService, NewProject}
import porchgeese.veb.http._
import porchgeese.veb.model.{ProjectId, ProjectName}
import porchgeese.veb.service.{ProjectsService, ServicesService}

class ProjectRoutes(projectService: ProjectsService, serviceService: ServicesService) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      (for {
        request <- req.as[NewProject]
        result  <- projectService.create(ProjectName(request.name))
      } yield {
        result
      }).flatMap(Ok(_))
    case req @ POST -> Root / UUIDVar(projectId) / "services" =>
      (for {
        request <- req.as[NewService]
        result  <- serviceService.create(ProjectId(projectId), request.position, request.name, request.serviceType)
      } yield {
        result
      }).flatMap(Ok(_))
  }
}

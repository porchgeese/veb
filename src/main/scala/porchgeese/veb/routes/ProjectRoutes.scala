package porchgeese.veb.routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import porchgeese.veb.http.Request.NewProject
import porchgeese.veb.http._
import porchgeese.veb.model.ProjectName
import porchgeese.veb.service.ProjectService

class ProjectRoutes(projectService: ProjectService) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      (for {
        request <- req.as[NewProject]
        result  <- projectService.create(ProjectName(request.name))
      } yield {
        result
      }).flatMap(Ok(_))
  }
}

package porchgeese.veb.routes

import cats.effect.IO
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.io._
import porchgeese.veb.http.Request.{NewProject, NewService, ServicePositionUpdate}
import porchgeese.veb.http._
import porchgeese.veb.model.{ProjectId, ProjectName, ServiceId}
import porchgeese.veb.service.{ProjectsService, ServicesService}
import cats.implicits._
import cats._
import cats.effect._
import cats.effect.implicits._
import com.typesafe.scalalogging.StrictLogging
class ProjectRoutes(projectService: ProjectsService, serviceService: ServicesService) extends StrictLogging {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case req @ POST -> Root =>
      (for {
        request <- req.as[NewProject]
        result  <- projectService.create(ProjectName(request.name))
      } yield {
        result
      }).flatMap(Created(_))

    case _ @GET -> Root / UUIDVar(projectId) =>
      (for {
        result <- projectService.find(ProjectId(projectId))
      } yield {
        result
      }).flatMap {
        case Some(project) => Ok(project)
        case None          => NotFound(s"Project $projectId not found.")
      }

    case req @ POST -> Root / UUIDVar(projectId) / "services" =>
      (for {
        request <- req.as[NewService]
        result  <- serviceService.create(ProjectId(projectId), request.position, request.dimension, request.name, request.serviceType)
      } yield {
        result
      }).flatMap(Created(_))

    case req @ PATCH -> Root / UUIDVar(_) / "services" / UUIDVar(serviceId) / "position" =>
      (for {
        request <- req.as[ServicePositionUpdate]
        result  <- serviceService.updatePosition(request.position)(ServiceId(serviceId))
      } yield {
        result
      }).flatMap {
        case Some(service) => Ok(service)
        case _             => NotFound(s"Service $serviceId not found.")
      }
  }
}

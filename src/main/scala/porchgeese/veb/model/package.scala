package porchgeese.veb

import java.time.Instant
import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
import io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
import io.circe.generic.semiauto._
import io.circe.Encoder._
import io.circe.Decoder._

package object model {
  type Coordinate = Double
  type Width      = Double
  type Length     = Double
  type Identifier = UUID

  case class ServiceType(value: String) extends AnyVal
  object ServiceType {
    implicit val decoder: Decoder[ServiceType] = deriveUnwrappedDecoder[ServiceType]
    implicit val encoder: Encoder[ServiceType] = deriveUnwrappedEncoder[ServiceType]
  }

  case class ServiceId(value: Identifier)
  object ServiceId {
    implicit val decoder: Decoder[ServiceId] = deriveUnwrappedDecoder[ServiceId]
    implicit val encoder: Encoder[ServiceId] = deriveUnwrappedEncoder[ServiceId]

  }

  case class ProjectId(value: Identifier)
  object ProjectId {
    implicit val decoder: Decoder[ProjectId] = deriveUnwrappedDecoder[ProjectId]
    implicit val encoder: Encoder[ProjectId] = deriveUnwrappedEncoder[ProjectId]

  }

  case class ServiceName(value: String) extends AnyVal
  object ServiceName {
    implicit val decoder: Decoder[ServiceName] = deriveUnwrappedDecoder[ServiceName]
    implicit val encoder: Encoder[ServiceName] = deriveUnwrappedEncoder[ServiceName]
  }

  case class ProjectName(value: String) extends AnyVal
  object ProjectName {
    implicit val decoder: Decoder[ProjectName] = deriveUnwrappedDecoder[ProjectName]
    implicit val encoder: Encoder[ProjectName] = deriveUnwrappedEncoder[ProjectName]
  }

  case class Position(x: Coordinate, y: Coordinate)
  object Position {
    implicit val decoder: Decoder[Position] = deriveDecoder[Position]
    implicit val encoder: Encoder[Position] = deriveEncoder[Position]
  }

  case class Dimension(width: Width, length: Length)
  object Dimension {
    implicit val decoder: Decoder[Dimension] = deriveDecoder[Dimension]
    implicit val encoder: Encoder[Dimension] = deriveEncoder[Dimension]
  }

  case class Service(id: ServiceId,
                     project: ProjectId,
                     position: Position,
                     dimension: Dimension,
                     name: String,
                     kind: ServiceType,
                     created: Instant,
                     updated: Instant)
  object Service {
    implicit val decoder: Decoder[Service] = deriveDecoder[Service]
    implicit val encoder: Encoder[Service] = deriveEncoder[Service]
  }

  case class Project(id: ProjectId, name: ProjectName, created: Instant, updated: Instant)
  object Project {
    implicit val decoder: Decoder[Project] = deriveDecoder[Project]
    implicit val encoder: Encoder[Project] = deriveEncoder[Project]
  }

  case class ProjectWithServices(id: ProjectId, name: ProjectName, services: List[Service], created: Instant, updated: Instant)

  object ProjectWithServices {
    implicit val decoder: Decoder[ProjectWithServices] = deriveDecoder[ProjectWithServices]
    implicit val encoder: Encoder[ProjectWithServices] = deriveEncoder[ProjectWithServices]
    def from(project: Project, services: List[Service]): ProjectWithServices =
      ProjectWithServices(project.id, project.name, services, project.created, project.updated)
  }

}

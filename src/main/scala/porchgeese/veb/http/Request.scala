package porchgeese.veb.http

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import porchgeese.veb.model.{Dimension, Position, ServiceType}

object Request {
  case class NewProject(name: String)
  object NewProject {
    implicit val encoder: Encoder[NewProject] = deriveEncoder[NewProject]
    implicit val decoder: Decoder[NewProject] = deriveDecoder[NewProject]
  }

  case class NewService(position: Position, dimension: Dimension, name: String, serviceType: ServiceType)
  object NewService {
    implicit val encoder: Encoder[NewService] = deriveEncoder[NewService]
    implicit val decoder: Decoder[NewService] = deriveDecoder[NewService]
  }

  case class ServicePositionUpdate(position: Position)
  object ServicePositionUpdate {
    implicit val encoder: Encoder[ServicePositionUpdate] = deriveEncoder[ServicePositionUpdate]
    implicit val decoder: Decoder[ServicePositionUpdate] = deriveDecoder[ServicePositionUpdate]
  }
}

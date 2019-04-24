package porchgeese.veb.http

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import porchgeese.veb.model.{Position, ServiceType}

object Request {
  case class NewProject(name: String)
  object NewProject {
    implicit val encoder: Encoder[NewProject] = deriveEncoder[NewProject]
    implicit val decoder: Decoder[NewProject] = deriveDecoder[NewProject]
  }

  case class NewService(position: Position, name: String, serviceType: ServiceType)
  object NewService {
    implicit val encoder: Encoder[NewService] = deriveEncoder[NewService]
    implicit val decoder: Decoder[NewService] = deriveDecoder[NewService]
  }
}

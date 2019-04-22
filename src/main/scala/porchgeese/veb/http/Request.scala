package porchgeese.veb.http

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._

object Request {
  case class NewProject(name: String)
  object NewProject {
    implicit val encoder: Encoder[NewProject] = deriveEncoder[NewProject]
    implicit val decoder: Decoder[NewProject] = deriveDecoder[NewProject]
  }
}

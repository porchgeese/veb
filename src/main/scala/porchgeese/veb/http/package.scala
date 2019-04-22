package porchgeese.veb

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}

package object http {
  implicit def jsonEncoderOf[A](implicit enc: Encoder[A]): EntityEncoder[IO, A] = org.http4s.circe.jsonEncoderOf[IO, A](implicitly, enc)
  implicit def jsonDecoderOf[A](implicit dec: Decoder[A]): EntityDecoder[IO, A] = org.http4s.circe.jsonOf[IO, A](implicitly, dec)
}

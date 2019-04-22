package porchgeese.veb.service

import java.util.UUID

import cats.effect.IO

trait UUIDService {
  def generate: IO[UUID]
}

object UUIDService {
  def apply(): UUIDService = new UUIDService() {
    override def generate: IO[UUID] = IO(UUID.randomUUID())
  }
}

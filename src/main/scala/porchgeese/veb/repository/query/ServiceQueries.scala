package porchgeese.veb.repository.query

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import porchgeese.veb.model.Service

object ServiceQueries {
  val tableName = fr"services"

  def insert(service: Service): doobie.ConnectionIO[Int] =
    sql"""
        |INSERT INTO services (
        | id,
        | project_id,
        | coordinate_x,
        | coordinate_y,
        | name,
        | kind,
        | created,
        | updated
        | ) VALUES(
        |   ${service.id},
        |   ${service.project},
        |   ${service.position.x},
        |   ${service.position.y},
        |   ${service.name},
        |   ${service.kind},
        |   ${service.created},
        |   ${service.updated}
        |  )
      """.stripMargin.update.run

}

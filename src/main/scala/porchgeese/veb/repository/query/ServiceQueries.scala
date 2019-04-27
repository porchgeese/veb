package porchgeese.veb.repository.query

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import porchgeese.veb.model.{ProjectId, Service, ServiceId}

object ServiceQueries {
  val tableName = fr"services"
  def update(serviceId: ServiceId)(service: Service): doobie.ConnectionIO[Int] =
    sql"""
      UPDATE
        services
      SET
        project_id = ${service.project},
        coordinate_x = ${service.position.x},
        coordinate_y = ${service.position.y},
        width = ${service.dimension.width},
        length = ${service.dimension.length},
        name = ${service.name},
        kind = ${service.kind},
        created = ${service.created},
        updated = ${service.updated}
      WHERE
        id = ${serviceId}
    """.stripMargin.update.run

  def insert(service: Service): doobie.ConnectionIO[Int] =
    sql"""
        |INSERT INTO services (
        | id,
        | project_id,
        | coordinate_x,
        | coordinate_y,
        | width,
        | length,
        | name,
        | kind,
        | created,
        | updated
        | ) VALUES(
        |   ${service.id},
        |   ${service.project},
        |   ${service.position.x},
        |   ${service.position.y},
        |   ${service.dimension.width},
        |   ${service.dimension.length},
        |   ${service.name},
        |   ${service.kind},
        |   ${service.created},
        |   ${service.updated}
        |  )
      """.stripMargin.update.run

  def findServicesFor(projectId: ProjectId): ConnectionIO[List[Service]] =
    sql"""
      SELECT
          id,
          project_id,
          coordinate_x,
          coordinate_y,
          width,
          length,
          name,
          kind,
          created,
          updated
      FROM
        services
      WHERE
        project_id = ${projectId}
    """.stripMargin.query[Service].to[List]

  def findService(serviceId: ServiceId): ConnectionIO[Option[Service]] =
    sql"""
      SELECT
          id,
          project_id,
          coordinate_x,
          coordinate_y,
          width,
          length,
          name,
          kind,
          created,
          updated
      FROM
        services
      WHERE
        id = ${serviceId}
    """.stripMargin.query[Service].option

}

package porchgeese.veb.repository.query

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import porchgeese.veb.model.Project

object ProjectQueries {
  def insert(project: Project): doobie.ConnectionIO[Int] =
    sql"""
        |INSERT INTO projects (id, name, created, updated) VALUES(
        | ${project.id},
        | ${project.name},
        | ${project.created},
        | ${project.updated})
      """.stripMargin.update.run

}

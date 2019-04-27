package porchgeese.veb.repository.query

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import porchgeese.veb.model.{Project, ProjectId}

object ProjectQueries {
  def findProject(projectId: ProjectId): ConnectionIO[Option[Project]] =
    sql"""
      SELECT
        id, name, created, updated
      FROM
        projects
      WHERE
        id = ${projectId.value}
    """.stripMargin.query[Project].option

  def insert(project: Project): ConnectionIO[Int] =
    sql"""
        |INSERT INTO projects (id, name, created, updated) VALUES(
        | ${project.id},
        | ${project.name},
        | ${project.created},
        | ${project.updated})
      """.stripMargin.update.run

}

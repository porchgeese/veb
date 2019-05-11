package porchgeese.veb.model

import java.util.UUID

object Filters {
  case class ProjectFilter(projectId: Option[UUID])
  object ProjectFilter {
    def empty = ProjectFilter(None)
  }
}

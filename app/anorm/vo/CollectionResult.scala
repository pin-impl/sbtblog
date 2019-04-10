package anorm.vo

import play.api.libs.json.Json

case class CollectionResult[T](
                               data: List[T],
                               next: Long
                               )
object CollectionResult {
  implicit val writers = Json.writes[CollectionResult[BlogSummary]]
}
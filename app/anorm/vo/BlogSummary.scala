package anorm.vo

import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.libs.json.{JodaReads, JodaWrites, Json, Writes}



case class BlogSummary(
                        id: Long,
                        title: String,
                        author: String,
                        createTime: DateTime
                      )

object BlogSummary {

  val parser: RowParser[BlogSummary] = {
    long("id") ~
    str("title") ~
    str("author") ~
    get[DateTime]("create_time") map {
      case id ~ title ~ author ~ createTime => BlogSummary(id, title, author, createTime)
    }
  }

  val listParser: ResultSetParser[List[BlogSummary]] = parser *

  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val writers = Json.writes[BlogSummary]
}
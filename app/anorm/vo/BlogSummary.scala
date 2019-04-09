package anorm.vo

import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._
import org.joda.time.DateTime

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
}
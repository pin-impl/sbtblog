package anorm.model

import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._

case class Blog(
               id: Long,
               title: String,
               content: String,
               viewCount: Long
               )

object Blog {
  val parser: RowParser[Blog] = {
    long("blog.id") ~
      str("blog.title") ~
      str("blog.content") ~
      long("blog.read_count") map {
      case id ~ title ~ content ~ viewCount => Blog(id, title, content, viewCount)
    }
  }

  val listParser: ResultSetParser[List[Blog]] = {
    parser *
  }
}
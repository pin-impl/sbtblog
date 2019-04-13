package anorm.model

import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._
import org.joda.time.DateTime

case class Blog(
               id: Long,
               title: String,
               summary: String,
               content: String,
               image: String,
               viewCount: Long,
               updateTime: DateTime,
               createTime: DateTime
               )

object Blog {
  val parser: RowParser[Blog] = {
    long("id") ~
      str("title") ~
      str("summary") ~
      str("content") ~
      str("image") ~
      long("view_count")~
      get[DateTime]("update_time") ~
      get[DateTime]("create_time") map {
      case id ~ title ~ summary ~ content ~ image ~ viewCount ~ updateTime ~ createTime => Blog(id, title, summary, content, image, viewCount, updateTime, createTime)
    }
  }

  val listParser: ResultSetParser[List[Blog]] = {
    parser *
  }

}
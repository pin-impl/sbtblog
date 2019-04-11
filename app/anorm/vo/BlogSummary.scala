package anorm.vo

import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.libs.json.{JodaReads, JodaWrites, Json, Writes}



case class BlogSummary(
                        id: Long,
                        title: String,
                        image: String,
                        summary: String,
                        createTime: DateTime
                      )

object BlogSummary {

  val parser: RowParser[BlogSummary] = {
    long("id") ~
    str("title") ~
    str("image") ~
    str("summary") ~
    get[DateTime]("create_time") map {
      case id ~ title ~ image ~ summary ~ createTime => BlogSummary(id, title, image, summary, createTime)
    }
  }

  val listParser: ResultSetParser[List[BlogSummary]] = parser *

  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("yyyy-MM-dd HH:mm:ss")
}


case class BlogDetail(
                     id: Long,
                     title: String,
                     image: String,
                     content: String,
                     createTime: DateTime
                     )
object BlogDetail {
  val parser: RowParser[BlogDetail] = {
    long("id") ~
    str("title") ~
    str("image") ~
    str("content") ~
    get[DateTime]("create_time") map {
      case id ~ title ~ image ~ content ~ createTime => BlogDetail(id, title, image, content, createTime)
    }
  }

  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("yyyy-MM-dd HH:mm:ss")

}
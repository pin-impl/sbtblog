package anorm.vo

import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._
import org.joda.time.DateTime
import anorm.AnormExtension._
import anorm.model.Blog
import play.api.libs.json.{Format, JodaReads, JodaWrites, Json}


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

}

case class PublishBlog(
                      title: String,
                      summary: String,
                      content: String
                      )
object PublishBlog {
  val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  implicit val dateFormat = Format[DateTime](JodaReads.jodaDateReads(pattern), JodaWrites.jodaDateWrites(pattern))
  implicit val fmt = Json.format[PublishBlog]
}
package anorm.vo

import play.api.libs.functional.syntax._
import anorm.{ResultSetParser, RowParser, ~}
import anorm.SqlParser._
import org.joda.time.DateTime
import anorm.AnormExtension._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{Format, JodaReads, JodaWrites, JsPath, Json, Writes}


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
                     ) {
  def this(id: Long, p: PublishBlog) {
    this(
      id,
      title = p.title,
      "",
      content = p.content,
      DateTime.now
    )
  }
}
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
  val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  implicit val dateFormat = Format[DateTime](JodaReads.jodaDateReads(pattern), JodaWrites.jodaDateWrites(pattern))
  implicit val fmt = Json.format[BlogDetail]

}

case class PublishBlog(
                      id: Option[Long],
                      title: String,
                      summary: String,
                      content: String
                      )
object PublishBlog {
  val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  implicit val dateFormat = Format[DateTime](JodaReads.jodaDateReads(pattern), JodaWrites.jodaDateWrites(pattern))
  implicit val fmt = Json.format[PublishBlog]
}

case class BlogTitle(id: Long, title: String)
object BlogTitle {
  var parser: RowParser[BlogTitle] = {
    long("id") ~
    str("title") map {
      case id ~ title => BlogTitle(id, title)
    }
  }

  implicit val write: Writes[BlogTitle] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "title").write[String]
    )(unlift(BlogTitle.unapply))
}
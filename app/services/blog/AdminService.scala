package services.blog

import anorm.model.Blog
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.db.Database
import anorm.SQL
import anorm.SqlParser.scalar
import org.joda.time.DateTime
import anorm.AnormExtension._

@Singleton
class AdminService @Inject() (db: Database) {
  val log = Logger(this.getClass)

  def saveBlog(blog: Blog): Option[Blog] = {
    db.withConnection { implicit conn =>
      val key = SQL(
        """
          |insert into blog
          |(title, summary, content, image, view_count, create_time, update_time)
          |values
          |({title}, {summary}, {content}, {image}, {viewCount}, {createTime}, {updateTime})
        """.stripMargin).on("title" -> blog.title,
        "summary" -> blog.summary, "content" -> blog.content, "image" -> blog.image,
      "viewCount" -> blog.viewCount, "createTime" -> DateTime.now, "updateTime" -> DateTime.now).executeInsert(scalar[Long].single)

      SQL(
        """
          |select * from blog where id = {id}
        """.stripMargin).on("id" -> key).as(Blog.parser.singleOpt)
    }
  }

}

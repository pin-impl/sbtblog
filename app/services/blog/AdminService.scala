package services.blog

import anorm.model.Blog
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.db.Database
import anorm.SQL
import anorm.SqlParser.scalar
import org.joda.time.DateTime
import anorm.AnormExtension._
import anorm.vo.PublishBlog

@Singleton
class AdminService @Inject() (db: Database) {
  val log = Logger(this.getClass)

  def saveBlog(blog: PublishBlog): Long = {
    db.withConnection { implicit conn =>
      SQL(
        """
          |insert into blog
          |(title, summary, content, image, view_count, create_time, update_time)
          |values
          |({title}, {summary}, {content}, {image}, {viewCount}, {createTime}, {updateTime})
        """.stripMargin).on("title" -> blog.title,
        "summary" -> blog.summary, "content" -> blog.content, "image" -> "",
      "viewCount" -> 0, "createTime" -> DateTime.now, "updateTime" -> DateTime.now).executeInsert(scalar[Long].single)

    }
  }

}

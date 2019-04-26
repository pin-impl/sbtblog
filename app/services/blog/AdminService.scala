package services.blog

import action.User
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.db.Database
import anorm.SQL
import anorm.SqlParser.scalar
import org.joda.time.DateTime
import anorm.AnormExtension._
import anorm.vo.{BlogDetail, PublishBlog}

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

  def getBlogById(id: Long): Option[BlogDetail] = {
    db.withConnection { implicit conn =>
      SQL(
        """
          |select title, id, summary, content, image, create_time from blog where id = {id}
        """.stripMargin).on("id" -> id).as(BlogDetail.parser.singleOpt)
    }
  }

  def updateBlog(blog: BlogDetail): Int = {
    db.withConnection { implicit conn =>
      SQL(
        """
          |update blog set title = {title}, content = {content}, update_time = now() where id = {id}
        """.stripMargin).on("title" -> blog.title, "content" -> blog.content, "id" -> blog.id)
        .executeUpdate()
    }
  }

  def loginUser(username: String, password: String): Option[User] = {
    db.withConnection { implicit conn =>
      SQL(
        """
          |select username, id from user where username = {username} and password = {password}
        """.stripMargin).on("username" -> username, "password" -> password).as(User.parser.singleOpt)
    }
  }

}

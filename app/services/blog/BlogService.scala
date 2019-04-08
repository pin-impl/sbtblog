package services.blog



import play.api.db.Database
import anorm.SQL
import anorm.SqlParser._
import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime

@Singleton
class BlogService @Inject() (db: Database) {



  def listBlog = {
    db.withConnection { implicit connection =>
      SQL("select title, read_count, create_time from blog").as(
        (str("title")
          ~ str("read_count")
          ~ get[DateTime]("create_time") map flatten) *)

    }


  }
}

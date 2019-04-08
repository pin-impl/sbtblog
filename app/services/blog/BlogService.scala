package services.blog



import play.api.db.Database
import anorm.SQL
import anorm.model.Blog
import javax.inject.{Inject, Singleton}

@Singleton
class BlogService @Inject() (db: Database) {



  def listBlog: List[Blog] = {
    db.withConnection { implicit connection =>
      SQL("select id, title, content, read_count from blog").as(Blog.listParser)
    }
  }

}

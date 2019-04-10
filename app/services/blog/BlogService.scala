package services.blog

import play.api.db.Database
import anorm.SQL
import anorm.model.Blog
import anorm.vo.BlogSummary
import javax.inject.{Inject, Singleton}

@Singleton
class BlogService @Inject() (db: Database) {

  def blogList: List[Blog] = {
    db.withConnection { implicit connection =>
      SQL("select id, title, content, read_count from blog").as(Blog.listParser)
    }
  }

  def blogSummaryList(next: Long = 0, size: Int = 10): List[BlogSummary] = {
    db.withConnection { implicit conn =>
      SQL("select id, title, author, create_time from blog where id > {next} limit {size}")
        .on("next" -> next, "size" -> size).as(BlogSummary.listParser)
    }
  }

}

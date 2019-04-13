package services.blog

import play.api.db.Database
import anorm.SQL
import anorm.vo.{BlogDetail, BlogSummary}
import javax.inject.{Inject, Singleton}

@Singleton
class BlogService @Inject() (db: Database) {

  def blogSummaryList(next: Long = 0, size: Int = 10): List[BlogSummary] = {
    db.withConnection { implicit conn =>
      SQL("select id, title, image, summary, create_time from blog where id > {next} limit {size}")
        .on("next" -> next, "size" -> size).as(BlogSummary.listParser)
    }
  }

  def blogDetail(id: Long): Option[BlogDetail] = {
    db.withConnection {implicit conn =>
      SQL(
        """
          | select id, title, image, content, create_time from blog where id = {id}
        """.stripMargin).on("id" -> id).as(BlogDetail.parser.singleOpt)
    }
  }

}

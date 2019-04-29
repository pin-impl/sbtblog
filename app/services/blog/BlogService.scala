package services.blog

import play.api.db.Database
import anorm.SQL
import anorm.vo.{BlogDetail, BlogSummary, BlogTitle}
import javax.inject.{Inject, Singleton}
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

@Singleton
class BlogService @Inject() (db: Database)  {
  val logger = Logger(this.getClass)

  def blogSummaryList(next: Long = 0, size: Int = 10): List[BlogSummary] = {
    db.withConnection { implicit conn =>
      SQL("select id, title, image, summary, create_time from blog where id > {next} limit {size}")
        .on("next" -> next, "size" -> size).as(BlogSummary.listParser)
    }
  }

  def blogDetail(id: Long): Option[BlogDetail] = {
    db.withConnection {implicit conn =>
      val result = Future {
        SQL(
          """
            |update blog set view_count = view_count + 1 where id = {id}
          """.stripMargin).on("id" -> id).executeUpdate()
      }
      result.onComplete {
        case Success(_) => logger.info(s"blog $id view count increased.")
        case Failure(exception) => logger.error(exception.getLocalizedMessage)
      }
      SQL(
        """
          | select id, title, image, content, create_time from blog where id = {id}
        """.stripMargin).on("id" -> id).as(BlogDetail.parser.singleOpt)
    }
  }

  def search(keyword: String): List[BlogSummary] = {
    db.withConnection { implicit conn =>
      SQL(
        """
          |select id, title, image, summary, create_time
          |from blog
          |where title like %{key}% or content like %{key}% order by id desc limit 10
        """.stripMargin).on("key" -> keyword).as(BlogSummary.listParser)
    }
  }

  def searchTitle(keyword: String): List[BlogTitle] = {
    db.withConnection {implicit conn =>
      SQL(
        """
          |select id, title from blog where title like {keyword} order by id desc limit 10
        """.stripMargin).on("keyword" -> s"%$keyword%").as(BlogTitle.parser *)
    }
  }

}

package controllers.blog

import anorm.vo.{BlogSummary, CollectionResult}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.BlogService

@Singleton
class BlogController @Inject() (cc: ControllerComponents, blogService: BlogService) extends AbstractController(cc) {

  val log = Logger(this.getClass)

  def blogList = Action {
    val blogs = blogService.blogList
    log.debug("" + blogs.mkString)
    Ok(views.html.index(blogs.mkString))
  }

  def listSummary(next: Long = 0) = Action {
    val summaryList = blogService.blogSummaryList()
    val next = summaryList.lastOption.map(s => s.id).getOrElse(0L)
    val result = CollectionResult(summaryList, next)
    log.debug(result.toString)
    Ok(views.html.list(result))
  }

  def blog(id: Long) = Action {
    val blog = blogService.blogDetail(id)
    blog map { b =>
      Ok(views.html.article(b))
    } getOrElse {
      Redirect("/blog/summary/list")
    }
  }

}

package controllers.blog

import anorm.vo.CollectionResult
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.BlogService

@Singleton
class BlogController @Inject() (cc: ControllerComponents, blogService: BlogService) extends AbstractController(cc) {

  def blogList = Action {
    val blogs = blogService.blogList
    Logger.debug("" + blogs.mkString)
    Ok(views.html.index(blogs.mkString))
  }

  def listSummary(next: Long = 0) = Action {
    val summaryList = blogService.blogSummaryList()
    val next = summaryList.lastOption.map(s => s.id).getOrElse(0L)
    val result = CollectionResult(summaryList, next)
    Logger.debug(result.toString)
    Ok(Json.toJson(result))
  }

}

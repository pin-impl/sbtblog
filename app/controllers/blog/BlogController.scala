package controllers.blog

import anorm.vo.{BlogSummary, CollectionResult}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.BlogService

@Singleton
class BlogController @Inject() (cc: ControllerComponents, blogService: BlogService) extends AbstractController(cc) {

  val log = Logger(this.getClass)

  def listSummary(next: Long = 0) = Action {
    val summaryList = blogService.blogSummaryList(next)
    val nextId = summaryList.lastOption.map(s => s.id).getOrElse(0L)
    val result = CollectionResult(summaryList, nextId)
    log.debug(result.toString)
    Ok(views.html.list(result))
  }

  def blog(id: Long) = Action {
    val blog = blogService.blogDetail(id)
    blog map { b =>
      Ok(views.html.article(b))
    } getOrElse {
      Redirect("/blogs")
    }
  }

  def search(keyword: String) = Action {
    val searchList = blogService.search(keyword)
    val result = CollectionResult(searchList, 0)
    Ok(views.html.list(result))
  }

  def searchTitle(keyword: String) = Action {
    val titles = blogService.searchTitle(keyword)
    Ok(Json.obj("list" -> titles))
  }

}

package controllers.blog

import javax.inject.{Inject, Singleton}
import org.slf4j.LoggerFactory
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.BlogService

@Singleton
class BlogController @Inject() (cc: ControllerComponents, blogService: BlogService) extends AbstractController(cc) {

  val log = LoggerFactory.getLogger(this.getClass)

  def blogList = Action {
    val blogs = blogService.listBlog
    log.info("{}", blogs)
    Ok(views.html.index)
  }

}

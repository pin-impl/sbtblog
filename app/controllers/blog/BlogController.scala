package controllers.blog

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.BlogService

@Singleton
class BlogController @Inject() (cc: ControllerComponents, blogService: BlogService) extends AbstractController(cc) {

  def blogList = Action {
    val blogs = blogService.listBlog
    Logger.debug("" + blogs.mkString)
    Ok(views.html.index(blogs.mkString))
  }

}

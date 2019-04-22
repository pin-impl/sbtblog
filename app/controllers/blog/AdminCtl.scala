package controllers.blog

import anorm.model.Blog
import anorm.vo.{BlogDetail, PublishBlog}
import common.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.{AdminService, BlogService}


@Singleton
class AdminCtl @Inject() (cc: ControllerComponents,
                          adminService: AdminService,
                          blogService: BlogService)
  extends AbstractController(cc) with play.api.Logging {


  def publishPage = Action {

      Ok(views.html.admin.publish(Option.empty))
  }

  def publishBlog = Action(parse.json) { implicit request =>
      request.body.validate[PublishBlog].fold(
        errors => BadRequest(errors.mkString),
        blog => {
          logger.info("blog param to publish or edit. " + blog.toString)
          val blogId = blog.id.map {id =>
            adminService.updateBlog(new BlogDetail(id, blog))
            id
          } getOrElse  {
            adminService.saveBlog(blog)
          }
          logger.info(s"success add a blog. $blogId")
          Ok(Json.obj("url" -> s"/blog/$blogId"))
        }
      )
  }

  def toEditBlog(id: Long) = Action { implicit request =>
    val blog = blogService.blogDetail(id)
    Ok(views.html.admin.publish(blog))
  }

  def editBlog = Action(parse.json) { implicit request =>
    request.body.validate[BlogDetail].fold(
      errors => BadRequest(errors.mkString),
      blog => {
        Ok
      }
    )
  }
}

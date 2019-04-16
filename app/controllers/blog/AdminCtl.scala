package controllers.blog

import anorm.model.Blog
import anorm.vo.PublishBlog
import common.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.AdminService


@Singleton
class AdminCtl @Inject() (cc: ControllerComponents, adminService: AdminService)
  extends AbstractController(cc) with play.api.Logging {


  def publishPage = Action {

      Ok(views.html.admin.publish())
  }

  def publishBlog = Action(parse.json) { implicit request =>
      request.body.validate[PublishBlog].fold(
        errors => BadRequest(errors.mkString),
        blog => {
          val id = adminService.saveBlog(blog)
          logger.info(s"success add a blog. $id")
          Ok(Json.obj("url" -> s"/blog/$id"))
        }
      )
  }
}

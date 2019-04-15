package controllers.blog

import anorm.model.Blog
import anorm.vo.PublishBlog
import common.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.AdminService


@Singleton
class AdminCtl @Inject() (cc: ControllerComponents, adminService: AdminService) extends AbstractController(cc) {


  def publishPage = Action {

      Ok(views.html.admin.publish())
  }

  def publishBlog = Action(parse.json) { implicit request =>
      request.body.validate[PublishBlog].fold(
        errors => BadRequest(errors.mkString),
        blog => {
          val id = adminService.saveBlog(blog)
          Ok(toJson("url" -> s"/blog/$id"))
        }
      )
  }
}

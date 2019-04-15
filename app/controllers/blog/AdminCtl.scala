package controllers.blog

import anorm.model.Blog
import common.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.blog.AdminService


@Singleton
class AdminCtl @Inject() (cc: ControllerComponents, adminService: AdminService) extends AbstractController(cc) {


  def publishPage = Action {

      Ok(views.html.admin.publish())
  }

  def publishBlog = AuthAction {
    Action(parse.json) { implicit request =>
      request.body.validate[Blog].fold(
        errors => BadRequest(errors.mkString),
        blog => {
          val id = adminService.saveBlog(blog)
          Redirect(s"/blog/$id")
        }
      )
    }
  }
}

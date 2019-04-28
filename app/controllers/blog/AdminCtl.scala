package controllers.blog

import anorm.vo.{BlogDetail, PublishBlog}
import action.{JwtAction, LoginUser, UserRequest}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Cookie}
import services.blog.{AdminService, BlogService}



@Singleton
class AdminCtl @Inject() (cc: ControllerComponents,
                          jwtAction: JwtAction,
                          adminService: AdminService,
                          blogService: BlogService)
  extends AbstractController(cc) with play.api.Logging {


  def publishPage = jwtAction { implicit request =>
    val req = request.asInstanceOf[UserRequest[_]]
    logger.info(s"[${req.user.username}] want to publish blog.")
    Ok(views.html.admin.publish(Option.empty))
  }

  def publishBlog = jwtAction(parse.json) { implicit request =>
      request.body.validate[PublishBlog].fold(
        errors => BadRequest(errors.mkString),
        blog => {
          logger.info(s"blog param to publish or edit. ${blog.toString}")
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

  def toEditBlog(id: Long) = jwtAction { implicit request =>
    val blog = blogService.blogDetail(id)
    Ok(views.html.admin.publish(blog))
  }

  def toLogin = Action {implicit request =>

    Ok(views.html.admin.login())
  }

  def login = Action(parse.json) { implicit request =>
    request.body.validate[LoginUser].fold(
      error => {
        logger.error(s"login error ${error.mkString}")
        Ok(Json.obj("url" -> "/login"))
      },
      loginUser => {
        adminService.loginUser(loginUser.user, loginUser.password).map(user => {
          val cookie = Cookie(name = "z-token", value = user.generateToken, maxAge = Some(60 * 60 * 24))
          Ok(Json.obj("url" -> s"/blogs")).withCookies(cookie)
        }) getOrElse {
          Ok(Json.obj("url" -> "/login"))
        }
      }
    )
  }
}

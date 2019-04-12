package controllers.blog

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}


@Singleton
class AdminCtl @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def publishPage = Action {

    Ok(views.html.admin.publish())
  }
}

package blog

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class BlogController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def blogList = Action {
    Ok(views.html.index(""))
  }

}

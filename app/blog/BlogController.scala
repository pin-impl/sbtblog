package blog

import javax.inject.{Inject, Singleton}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class BlogController @Inject() (cc: ControllerComponents, ws: WSClient) extends AbstractController(cc) {

  def blogList = Action {
    ws.url("http://baidu.com").addHttpHeaders("auth" -> "token")
        .get()
    Ok(views.html.index(""))
  }

}

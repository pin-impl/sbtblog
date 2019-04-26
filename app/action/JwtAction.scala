package action

import javax.inject.Inject
import pdi.jwt.{Jwt, JwtAlgorithm}
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Results.Ok

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class JwtAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  def apply[A](action: Action[A]) = async(action.parser) { request =>
    action(request)
  }

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val jwtToken = request.headers.get("jw_token").getOrElse("")
    Jwt.decodeRaw(jwtToken, "", Seq(JwtAlgorithm.HS256)) match {
      case Success(payload) => {
        val user = Json.parse(payload).validate[User].get
        val userRequest = UserRequest(user, request)
        block(userRequest)
      }
      case Failure(exception) => Future.successful(Ok())
    }
  }


}

case class User(email: String, userId: String)
case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)

object User {

  implicit val read: Reads[User] = (
    (JsPath \ "email").read[String] and
      (JsPath \ "userId").read[String]
  )(User.apply _)
  implicit val fmt = Json.format[User]
}

case class LoginUser(
                    user: String,
                    password: String
                    )
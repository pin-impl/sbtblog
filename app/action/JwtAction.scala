package action


import javax.inject.Inject
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Results.Redirect
import org.joda.time.DateTime
import util.Const
import anorm.{RowParser, ~}
import anorm.SqlParser._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class JwtAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  def apply[A](action: Action[A]) = async(action.parser) { request =>
    action(request)
  }

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val jwtToken = request.headers.get("jw_token").getOrElse("")
    Jwt.decodeRaw(jwtToken, Const.SECRET, Seq(JwtAlgorithm.HS256)) match {
      case Success(payload) => {
        val user = Json.parse(payload).validate[User].get
        val userRequest = UserRequest(user, request)
        block(userRequest)
      }
      case Failure(_) => {
        Future.successful(Redirect("/login"))
      }
    }
  }


}

case class User(email: String, userId: Long) {
  def generateToken: String = {
    val claim = JwtClaim(content = Json.toJson(this).toString,
      expiration = Some(DateTime.now.plusDays(7).getMillis),
      subject = Some("zp"))
    Jwt.encode(claim, Const.SECRET, JwtAlgorithm.HS256)
  }
}
case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)

object User {

  val parser: RowParser[User] = {
    long("id") ~
    str("email") map {
      case id ~ email => User(email, id)
    }
  }

  implicit val write: Writes[User] = (
    (JsPath \ "email").write[String] and
      (JsPath \ "userId").write[Long]
  )(unlift(User.unapply))

  implicit val read: Reads[User] = (
    (JsPath \ "email").read[String] and
      (JsPath \ "userId").read[Long]
  )(User.apply _)
  implicit val fmt = Json.format[User]
}

case class LoginUser(
                    user: String,
                    password: String
                    )
object LoginUser {
  implicit val fmt = Json.format[LoginUser]
}
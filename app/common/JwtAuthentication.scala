package common

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import pdi.jwt.{Jwt, JwtAlgorithm}
import play.api.libs.json.Json
import play.api.mvc.{ActionBuilder, Request, Result, WrappedRequest}

import scala.concurrent.Future
import scala.util.{Failure, Success}


case class User(email: String, userId: String)
case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)
object JWTAuthentication extends ActionBuilder[UserRequest] {
  def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    implicit val req = request
    val jwtToken = request.headers.get("jw_token").getOrElse("")

    Jwt.decodeRaw(jwtToken, "", Seq(JwtAlgorithm.HS256)) match {
      case Success(payload) => {
        val user = Json.parse(payload).validate[User].get
        block
      }
      case Failure(exception) => {
        Future.successful(exception)
      }
    }
  }
}

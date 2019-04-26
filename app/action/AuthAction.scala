package action

import play.api.mvc._
import play.api.mvc.Results.Forbidden

import scala.concurrent.Future


case class AuthAction[A](action: Action[A]) extends Action[A] with play.api.Logging {

  def apply(request: Request[A]): Future[Result] = {
    logger.info("Calling action")
    request.cookies.get("impl_token").map( t => {
      action(request)
    }) getOrElse action(request)//Future.successful(Forbidden)
  }

  override def parser = action.parser
  override def executionContext = action.executionContext
}


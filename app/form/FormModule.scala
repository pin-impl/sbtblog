package form

import action.LoginUser
import javax.inject.Singleton
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping

@Singleton
object FormModule {

  val userForm = Form(
    mapping(
      "user" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginUser.apply)(LoginUser.unapply)
  )

}

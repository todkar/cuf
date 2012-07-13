package controllers

import play.api._
import play.api.mvc._

object UserController extends Controller {

    def index = Action {
    Ok(views.html.user.index())
  }


}

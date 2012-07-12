package controllers

import play.api._
import play.api.mvc._

object AdminController extends Controller{

  def index = Action {
    Ok(views.html.admin.index())
  }

}
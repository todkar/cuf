package controllers

import play.api._
import play.api.mvc._

object MapsController extends Controller {
  
  def index = Action {
    Ok(views.html.maps.index("Maps are being built... Please be patient."))
  }

}
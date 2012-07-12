package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import org.neo4j.graphdb._

import models.CreditUnion

object CreditUnionController extends Controller {

  var creditUnions = List[CreditUnion]()

  val creditUnionForm = Form[CreditUnion](
    mapping(
      "name" -> nonEmptyText,
      "acceptsWorkingIn" -> nonEmptyText)(CreditUnion.apply)(CreditUnion.unapply))

  def index = Action {
    Ok(views.html.creditUnion.index(creditUnions))
  }

  def form = Action {
    Ok(views.html.creditUnion.form(creditUnionForm))
  }

  def create = Action { implicit request =>
    val creditUnion = creditUnionForm.bindFromRequest.get
    creditUnions = creditUnion :: creditUnions

    Ok(views.html.creditUnion.index(creditUnions))
  }
}
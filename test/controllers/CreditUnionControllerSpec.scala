package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CreditUnionControllerSpec extends Specification {
  
  "respond to the index Action" in {
  val Some(result) = routeAndCall(FakeRequest(GET, "/index"))

  status(result) must equalTo(OK)
  contentType(result) must beSome("text/html")
  charset(result) must beSome("utf-8")
  // contentAsString(result) must contain("Hello Bob")
}
}


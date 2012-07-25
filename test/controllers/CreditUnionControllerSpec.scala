package controllers

import play.api.test._
import play.api.test.Helpers._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Assertions

@RunWith(classOf[JUnitRunner])
class CreditUnionControllerSpec extends FunSuite with ShouldMatchers{

  test("respond to the index Action") {
    val Some(result) = routeAndCall(FakeRequest(GET, "/creditUnion"))

    status(result) should equal(OK)
    contentType(result) should be(Some("text/html"))
    charset(result) should be(Some("utf-8"))
  }
}


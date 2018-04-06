package unitTest.validator

import moe.brianhsu.gtd.validator._
import org.scalatest._

trait EmptyStringBehaviors { this: WordSpec with OptionValues with Matchers =>
  def emptyString(s: String) {
    "return an error message" in {
      val result = ParamValidator.NonEmptyString(s)
      result.value shouldBe IsRequired
    }
  }
}

class NonEmptyStringSpec extends WordSpec with Matchers with OptionValues with EmptyStringBehaviors {

  "NonEmptyString" when {
    "string is null" should { behave like emptyString(null) }
    "string is empty" should { behave like emptyString("") }
    "string is effectively empty" should { behave like emptyString("   ") }
    "string is not empty" should {
      "return none" in {
        val result = ParamValidator.NonEmptyString("HelloWorld")
        result.isDefined shouldBe false
      }
    }
  }

}


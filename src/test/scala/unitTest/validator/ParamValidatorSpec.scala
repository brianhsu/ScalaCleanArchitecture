package unitTest.validator

import moe.brianhsu.gtd.validator._

import org.scalatest._
import scala.language.existentials

class ParamValidatorSpec extends WordSpec with Matchers with OptionValues with EmptyStringBehaviors {

  import ParamValidator._

  object StringNot5 extends ErrorDescription
  object StringNot4 extends ErrorDescription
  object IntLessThen5 extends ErrorDescription
  object IntLessThen4 extends ErrorDescription

  val shouldBe5Chars: Validation[String] = (x: String) => if (x.length != 5) Some(StringNot5) else None
  val shouldBe4Chars: Validation[String] = (x: String) => if (x.length != 4) Some(StringNot4) else None
  val shouldLargeThen5: Validation[Int] = (x: Int) => if (x < 5) Some(IntLessThen5) else None
  val shouldLargeThen4: Validation[Int] = (x: Int) => if (x < 4) Some(IntLessThen4) else None

  "ParamValidatorSpec" when {
    "validate single request" should {

      "return Nil if every validation success" in {
        val result = forField("string", "12345", NonEmptyString, shouldBe5Chars)()

        result shouldBe Nil
      }

      "return list of one element if there is only one validation failed" in {
        val result = forField("string", "", NonEmptyString)()

        result shouldBe List(FieldError("string", IsRequired))
      }

      "return only the first validation error even there are multiple failure" in {
        val result = forField("string", "", shouldBe5Chars, shouldBe4Chars)()

        result shouldBe List(FieldError("string", StringNot5))
      }
    }

    "validate multiple field" should {
      "return None if every validation success" in {
        val result = ParamValidator.checkParams(
          forField("string", "12345", NonEmptyString, shouldBe5Chars),
          forField("integer", 100000, shouldLargeThen5, shouldLargeThen4)
        )

        result shouldBe None

      }

      "return ParamError contains list of failure for each failed field" in {

        val result = ParamValidator.checkParams(
          forField("string", "", NonEmptyString, shouldBe5Chars),
          forField("integer", 3, shouldLargeThen5, shouldLargeThen4)
        )

        val expectedFieldErrors = List(
          FieldError("string", IsRequired),
          FieldError("integer", IntLessThen5)
        )
        result.value.error shouldBe ParamError(expectedFieldErrors)

      }
    }
  }
}


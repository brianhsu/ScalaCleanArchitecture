package unitTest.usecase

import org.scalatest._

class CreateStuffSepec extends fixture.WordSpec with Matchers {

  "CreateStuffSpec" when {
    "validate request" should {

      "return an ParamError when title is empty" in { fixture =>
        pending
      }

      "return an ParamError when description is empty" in { fixture =>
        pending
      }

    }

    "execute the operation" should {
      "return stuff entity with correct information" in { fixture =>
        pending
      }
      "save to database" in { fixture =>
        pending
      }
    }

    "journaling" should {
      "return correct journal log entry" in { fixture =>
        pending
      }
    }
  }

  type FixtureParam = Int
  def withFixture(test: OneArgTest): Outcome = {
    test(1)
  }
}

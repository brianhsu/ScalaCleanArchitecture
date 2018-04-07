package unitTest.usecase

import moe.brianhsu.gtd.journal._
import moe.brianhsu.gtd.usecase._
import moe.brianhsu.gtd.validator._
import unitTest.stub._
import org.scalatest._

import scala.util._


class UseCaseExecutorSpec extends fixture.WordSpec with Matchers {

  class DoNothing extends BaseUseCase[Unit] {
    var isValidationCalled: Boolean = false

    override def validate(): Option[ValidationError] = {
      this.isValidationCalled = true
      None
    }

    override def execute(): Unit = {}
  }

  "UseCaseExecutor" when {

    "executing use case" should {

      "call validation function before execute" in { executor =>

        val useCase = new DoNothing

        executor.execute(useCase)

        useCase.isValidationCalled shouldBe true
      }

      "return an Failure[ValidationError] if validation function returning non-empty Option" in { executor =>
        val useCase = new DoNothing {
          override def validate(): Option[ValidationError] = {
            Some(new ValidationError(null))
          }
        }

        val result = executor.execute(useCase) 

        result.isFailure shouldBe true
        result.asInstanceOf[Failure[_]].exception shouldBe a[ValidationError]
      }

      "return an Failure[Exception] if exception is thrown in execute function" in { executor =>

        class SomeException extends Exception

        val useCase = new DoNothing {
          override def execute(): Unit= {
            throw new SomeException
          }
        }

        val result = executor.execute(useCase) 

        result.isFailure shouldBe true
        result.asInstanceOf[Failure[_]].exception shouldBe a[SomeException]
      }

      "return Success if validation is success and no exception in execute function" in { executor =>
        val useCase = new BaseUseCase[Int] {
          def execute() = 100
        }
      
        val result = executor.execute(useCase) 
        
        result shouldBe Success(100)
      }

    }

    "appending journal to system" should {

      "do nothing if journal is not provided" in { executor =>

        val useCase = new DoNothing

        executor.execute(useCase)

        executor.journals shouldBe Nil
      }

      "save journal to system if there is journal entry for the use case" in { executor =>

        val useCase1 = new DoNothing {
          override def journal = Some(DoNothing)
        }

        val useCase2 = new DoNothing {
          override def journal = Some(DoNothing)
        }

        executor.execute(useCase1)
        executor.execute(useCase2)

        executor.journals shouldBe List(DoNothing, DoNothing)
      }
    }

  }

  type FixtureParam = UseCaseExecutorLogToMemory


  override def withFixture(test: OneArgTest): Outcome = {
    val executor = new UseCaseExecutorLogToMemory
    test(executor)
  }
}

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

      "call validation function before execute" in { fixture =>
        val FixtureTestParam(executor, presenter) = fixture
        val useCase = new DoNothing

        executor.execute(useCase)(presenter)

        useCase.isValidationCalled shouldBe true
      }

      "return an Failure[ValidationError] if validation function returning non-empty Option" in { fixture =>
        val FixtureTestParam(executor, presenter) = fixture

        val useCase = new DoNothing {
          override def validate(): Option[ValidationError] = {
            Some(new ValidationError(null))
          }
        }

        executor.execute(useCase)(presenter)

        val result = presenter.getValue
        result.isFailure shouldBe true
        result.asInstanceOf[Failure[_]].exception shouldBe a[ValidationError]
      }

      "return an Failure[Exception] if exception is thrown in execute function" in { fixture =>
        val FixtureTestParam(executor, presenter) = fixture

        class SomeException extends Exception

        val useCase = new DoNothing {
          override def execute(): Unit= {
            throw new SomeException
          }
        }

        executor.execute(useCase)(presenter)

        val result = presenter.getValue
        result.asInstanceOf[Failure[_]].exception shouldBe a[SomeException]
      }

      "call presenter" in { fixture =>

        val FixtureTestParam(executor, presenter) = fixture
        val useCase = new DoNothing

        executor.execute(useCase)(presenter)

        presenter.isCalled shouldBe true
      }

      "return Success if validation is success and no exception in execute function" in { fixture =>

        val FixtureTestParam(executor, _) = fixture

        val useCase = new BaseUseCase[Int] {
          def execute() = 100
        }

        var isCalled = false
        var useCaseResult: Try[Int] = Failure(new NoSuchElementException)

        executor.execute(useCase) { result =>
          isCalled = true
          useCaseResult = result
        }

        isCalled shouldBe true
        useCaseResult shouldBe Success(100)
      }

    }

    "appending journal to system" should {

      "do nothing if journal is not provided" in { fixture =>

        val FixtureTestParam(executor, presenter) = fixture
        val useCase = new DoNothing

        executor.execute(useCase)(presenter)

        executor.journals shouldBe Nil
      }

      "save journal to system if there is journal entry for the use case" in { fixture =>

        val FixtureTestParam(executor, presenter) = fixture

        val useCase1 = new DoNothing {
          override def journal = Some(DoNothing)
        }

        val useCase2 = new DoNothing {
          override def journal = Some(DoNothing)
        }

        executor.execute(useCase1)(presenter)
        executor.execute(useCase2)(presenter)

        executor.journals shouldBe List(DoNothing, DoNothing)
      }
    }

  }

  type FixtureParam = FixtureTestParam

  case class FixtureTestParam(executor: UseCaseExecutorLogToMemory, presenter: LogValuePresenter[Unit])

  override def withFixture(test: OneArgTest): Outcome = {
    val executor = new UseCaseExecutorLogToMemory
    val presenter = new LogValuePresenter[Unit]
    val param = new FixtureParam(executor, presenter)
    test(param)
  }
}

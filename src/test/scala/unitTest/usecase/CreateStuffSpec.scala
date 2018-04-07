package unitTest.usecase

import moe.brianhsu.gtd.usecase._
import moe.brianhsu.gtd.validator._
import moe.brianhsu.gtd.entity._
import moe.brianhsu.gtd.journal._
import java.time._
import java.util.UUID

import moe.brianhsu.gtd.repo.memory.InMemoryInboxRepo
import org.scalatest._
import unitTest.stub._

class CreateStuffSpec extends fixture.WordSpec with Matchers with OptionValues {

  private val userUUID = UUID.fromString("2a53d868-0b7c-4058-b4fe-b727eb24da6f")

  "CreateStuffSpec" when {
    "validate request" should {

      "return an ParamError when title is empty" in { fixture =>
        val request = CreateStuff.Request(userUUID, title = "", description = "Description")
        val createStuff = fixture.makeCreateStuff(request)

        val error = createStuff.validate().value.error

        error shouldBe ParamError(List(FieldError("title", IsRequired)))
      }

      "return an ParamError when description is empty" in { fixture =>
        val request = CreateStuff.Request(userUUID, title = "Title", description = "")
        val createStuff = fixture.makeCreateStuff(request)

        val error = createStuff.validate().value.error

        error shouldBe ParamError(List(FieldError("description", IsRequired)))
      }

      "return None when everything is valid" in { fixture =>
        val request = CreateStuff.Request(userUUID, title = "Title", description = "Description")
        val createStuff = fixture.makeCreateStuff(request)

        val error = createStuff.validate()

        error shouldBe None
      }

    }

    "execute the operation" should {
      "return stuff entity with correct information" in { fixture =>
        val (request, expectedStuff) = fixture.makeRequest(fixture)
        val createStuff = fixture.makeCreateStuff(request)
        val createdStuff = createStuff.execute() 
        
        createdStuff shouldBe expectedStuff
      }

      "save to database" in { fixture =>
        val (request, expectedStuff) = fixture.makeRequest(fixture)
        val createStuff = fixture.makeCreateStuff(request)
        val createdStuff = createStuff.execute() 
        
        fixture.inboxRepo.find(createdStuff.uuid) shouldBe Some(expectedStuff)
      }
    }

    "journaling" should {
      "return correct journal log entry" in { fixture =>
        val (request, expectedStuff) = fixture.makeRequest(fixture)
        val createStuff = fixture.makeCreateStuff(request)
        val journal = createStuff.journal

        journal.value shouldBe InsertLog("inbox", expectedStuff.uuid, expectedStuff, expectedStuff.createTime)
      }
    }
  }

  type FixtureParam = TestFixture

  protected class TestFixture {
    implicit val generator: FixedDataGenerator = new FixedDataGenerator
    implicit val inboxRepo: InMemoryInboxRepo = new InMemoryInboxRepo

    def makeCreateStuff(request: CreateStuff.Request) = new CreateStuff(request)

    def makeRequest(fixture: FixtureParam): (CreateStuff.Request, Stuff) = {

      fixture.generator.currentTime.setTime(LocalDateTime.of(2018, 3, 1, 10, 0, 0))

      val createdUUID = fixture.generator.randomUUID.uuid
      val nowTime = fixture.generator.currentTime.time
      val expectedStuff = Stuff(createdUUID, userUUID, "Title", "Description", nowTime, nowTime)
      val request = CreateStuff.Request(userUUID, title = "Title", description = "Description")

      (request, expectedStuff)
    }
  }

  def withFixture(test: OneArgTest): Outcome = {
    test(new TestFixture)
  }
}

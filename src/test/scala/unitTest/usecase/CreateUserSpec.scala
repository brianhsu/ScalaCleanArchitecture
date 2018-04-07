package unitTest.usecase

import java.time._
import java.util.UUID

import moe.brianhsu.gtd.entity._
import moe.brianhsu.gtd.journal.InsertLog
import moe.brianhsu.gtd.repo.memory.InMemoryUser
import moe.brianhsu.gtd.usecase._
import moe.brianhsu.gtd.validator._
import org.scalatest._
import unitTest.stub._

class CreateUserSpec extends fixture.WordSpec with Matchers with OptionValues {

  "CreateUser" when {

    "validate request" should {
      "return an exception if email is empty" in { fixture =>
        val request = CreateUser.Request(email = "", name = "UserName")
        val createUser = fixture.makeCreateUser(request)

        val error = createUser.validate().value.error
        
        error shouldBe ParamError(List(FieldError("email", IsRequired)))
      }

      "return an exception if email is effectively empty" in { fixture =>
        val request = CreateUser.Request(email = "    ", name = "UserName")
        val createUser = fixture.makeCreateUser(request)

        val error = createUser.validate().value.error
        
        error shouldBe ParamError(List(FieldError("email", IsRequired)))
      }

      "return an exception if email is malformed" in { fixture =>
        val request = CreateUser.Request(email = "user#brian.moe", name = "Brian")
        val createUser = fixture.makeCreateUser(request)

        val error = createUser.validate().value.error
        
        error shouldBe ParamError(List(FieldError("email", IsMalformed)))
      }


      "return an exception if name is empty" in { fixture =>
        val request = CreateUser.Request(email = "user@example.com", name = "")
        val createUser = fixture.makeCreateUser(request)

        val error = createUser.validate().value.error
        
        error shouldBe ParamError(List(FieldError("name", IsRequired)))
      }

      "return an exception if name is effectively empty" in { fixture =>
        val request = CreateUser.Request(email = "user@example.com", name = "   ")
        val createUser = fixture.makeCreateUser(request)

        val error = createUser.validate().value.error
        
        error shouldBe ParamError(List(FieldError("name", IsRequired)))

      }

      "reutrn an exception if email is duplicate" in { fixture =>
        val existUserEmail = "user@example.com"
        val existUserUUID = UUID.fromString("1773a57c-004e-4f04-95fd-c9c9a7de4f92")

        fixture.userRepo.insert(User(existUserUUID, existUserEmail, "UserName"))

        val request = CreateUser.Request(existUserEmail, "AnotherUser")
        val createUser = fixture.makeCreateUser(request)

        val error = createUser.validate().value.error

        error shouldBe ParamError(List(FieldError("email", IsDuplicated)))
      }

      "return None when everything is validated" in { fixture =>
        val request = CreateUser.Request("user@example.com", "UserName")
        val createUser = fixture.makeCreateUser(request)

        val result = createUser.validate()

        result shouldBe None
      }

    }

    "generate journal" should {
      "return correct journal object" in { fixture =>
        val (request, expectedUser) = generateRequest(fixture)
        val createUser = fixture.makeCreateUser(request)
        val journal = createUser.journal
        val nowTime = fixture.dataGenerator.currentTime.time

        journal.value shouldBe InsertLog("user", expectedUser.uuid, expectedUser, nowTime)
      }
    }

    "execute the operation" should {
      "have correct uuid, name, email and set createTime / updateTime to current time" in { fixture =>
        val (request, expectedUser) = generateRequest(fixture)

        val createUser = fixture.makeCreateUser(request)
        val createdUser = createUser.execute()

        createdUser shouldBe expectedUser
      }

      "call DAO object to save" in { fixture =>

        val (request, expectedUser) = generateRequest(fixture)

        val createUser = fixture.makeCreateUser(request)
        val createdUser = createUser.execute()

        fixture.userRepo.find(createdUser.email) shouldBe Some(expectedUser)
      }
    }
  }

  private def generateRequest(fixture: TestFixture): (CreateUser.Request, User) = {
    val generatedUUID = fixture.dataGenerator.randomUUID.uuid
    val nowTime = LocalDateTime.of(2018, 10, 1, 10, 11, 11)
    val request = CreateUser.Request("user@example.com", "UserName")
    val expectedUser = User(generatedUUID, "user@example.com", "UserName", nowTime, nowTime)

    fixture.dataGenerator.currentTime.setTime(nowTime)

    (request, expectedUser)
  }

  type FixtureParam = TestFixture

  override def withFixture(test: OneArgTest) = test(new TestFixture)

  class TestFixture {
    implicit val dataGenerator = new FixedDataGenerator
    implicit val userRepo = new InMemoryUser

    def makeCreateUser(request: CreateUser.Request) = new CreateUser(request)
  }

}

package unitTest

import moe.brianhsu.gtd.validator._
import org.scalatest._

object DAO {
  trait UserRepo {
    def findUser(email: String): Option[User]
  }
}

trait DAO {
  def userRepo: DAO.UserRepo
}

class FakeUserRepo extends DAO.UserRepo {
  var user: List[User] = Nil
  override def findUser(email: String): Option[User] = user.filter(_.email == email).headOption
}

class DAOMock extends DAO {
  override val userRepo = new FakeUserRepo
}

import java.util.UUID
import moe.brianhsu.gtd.usecase._

case class User(uuid: UUID, email: String, name: String)

trait UUIDGenerator {
  def randomUUID: UUID
}

class FixedUUIDGenerator extends UUIDGenerator {
  private var uuid = UUID.fromString("ec0509a2-bb89-41a4-a8bb-b56816f61890")
  def randomUUID = uuid
  def setUUID(uuid: String) = {
    this.uuid = UUID.fromString(uuid)
  }
}

object CreateUser {
  case class Request(email: String, name: String)
}



class CreateUser(request: CreateUser.Request)(implicit val dao: DAO, implicit val uuidGenerator: UUIDGenerator) extends UseCase[User] {

  val uuid = uuidGenerator.randomUUID

  override def execute() = ???
  override def journal = ???
  override def validate(): Option[ValidationError] = {

    import ParamValidator._
    val checkEmailDuplicate = (email: String) => dao.userRepo.findUser(email).map(s => IsDuplicated)

    checkParams(
      forField("email", request.email, NonEmptyString, EmailValidator, checkEmailDuplicate),
      forField("name", request.name, ParamValidator.NonEmptyString)
    )
  }


}

class CreateUserSpec extends fixture.WordSpec with Matchers with OptionValues {

  "CreateUser" when {

    "initialize" should { 
      "setup UUID accoriding to UUID Generator" in { fixture =>
        val generatedUUID = fixture.uuidGenerator.randomUUID
        val createUser = fixture.makeCreateUser(CreateUser.Request(email = "user@example.com", "UserName"))

        createUser.uuid shouldBe generatedUUID
      }
    }

    "validation" should {
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
        fixture.userRepo.user ::= User(existUserUUID, existUserEmail, "UserName")

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

    "execute the operation" should {
      "set createTime / updateTime correctly" in { fixture =>
        pending
      }

      "generate journal object" in { fixture =>
        pending
      }

      "call DAO object to save" in { fixture =>
        pending
      }
    }
  }

  class TestFixture {
    implicit val dao = new DAOMock
    implicit val uuidGenerator = new FixedUUIDGenerator

    def userRepo = dao.userRepo.asInstanceOf[FakeUserRepo]

    def makeCreateUser(request: CreateUser.Request) = {
      import dao._
      import uuidGenerator._
      new CreateUser(request)
    }

  }

  type FixtureParam = TestFixture

  override def withFixture(test: OneArgTest) = {
    val fixture = new TestFixture
    test(fixture)
  }

}

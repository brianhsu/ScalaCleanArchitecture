package unitTest.usecase

import moe.brianhsu.gtd.usecase._
import moe.brianhsu.gtd.entity._
import moe.brianhsu.gtd.validator._
import java.time._
import java.util.UUID

import moe.brianhsu.gtd.journal.UpdateLog
import moe.brianhsu.gtd.repo.memory.InMemoryInboxRepo
import org.scalatest._
import unitTest.stub._

class MoveToTrashSpec extends fixture.WordSpec with Matchers with OptionValues {

  private val theUserUUID = UUID.fromString("2a53d868-0b7c-4058-b4fe-b727eb24da6f")
  private val theStuffUUID = UUID.fromString("7a632665-46bf-4766-8404-c191a2637632")

  private val nonExistUserUUID = UUID.fromString("e7fa1077-3fe2-4e23-ac28-fc837eef313d")
  private val nonExistUUID = UUID.fromString("bdc5a146-b5de-4969-93ca-6d43f0d98197")

  "MoveToTrash" when {
    "validate request" should {

      "return an ParamError when UUID is not found" in { fixture =>
        val moveToTrash = fixture.makeMoveToTrash(theUserUUID, nonExistUUID)
        val error = moveToTrash.validate().value.error

        error shouldBe ParamError(List(FieldError("uuid", NotFound)))
      }

      "return an ParamError when stuff is not match with user UUID" in { fixture =>

        val moveToTrash = fixture.makeMoveToTrash(nonExistUserUUID, theStuffUUID)
        val error = moveToTrash.validate().value.error

        error shouldBe ParamError(List(FieldError("userUUID", AccessDenied)))
      }

      "return None when everything is OK" in { fixture =>
        val moveToTrash = fixture.makeMoveToTrash(theUserUUID, theStuffUUID)
        val error = moveToTrash.validate()

        error shouldBe None
      }

    }

    "execute the operation" should {
      "update isTrash flag to true and update timestamp" in { fixture =>

        val updateTime = LocalDateTime.parse("2018-10-11T13:00:00")
        val moveToTrash = fixture.makeMoveToTrash(updateTime)

        val updatedStuff = moveToTrash.execute()

        updatedStuff.isTrash shouldBe true
        updatedStuff.createTime shouldBe fixture.createTime
        updatedStuff.updateTime shouldBe updateTime
      }

      "save to database" in { fixture =>

        val updateTime = LocalDateTime.parse("2018-10-11T13:00:00")
        val moveToTrash = fixture.makeMoveToTrash(updateTime)

        moveToTrash.execute()
        
        val stuffInDB = fixture.inboxRepo.find(theStuffUUID).value

        stuffInDB.isTrash shouldBe true
        stuffInDB.createTime shouldBe fixture.createTime
        stuffInDB.updateTime shouldBe updateTime
      }
    }

    "create journal" should {
      "return an update journal log entry" in { fixture =>

        val updateTime = LocalDateTime.parse("2018-10-11T13:00:00")
        val moveToTrash = fixture.makeMoveToTrash(updateTime)
        val updatedStuff = moveToTrash.execute()

        val journal = moveToTrash.journal

        journal.value shouldBe UpdateLog("inbox", theStuffUUID, updatedStuff, updateTime)
      }
    }
  }

  type FixtureParam = TestFixture

  protected class TestFixture {
    implicit val generator: FixedDataGenerator = new FixedDataGenerator
    implicit val inboxRepo: InMemoryInboxRepo = new InMemoryInboxRepo
    
    val createTime: LocalDateTime = LocalDateTime.parse("2018-01-01T10:00:00")
    val stuff = Stuff(theStuffUUID, theUserUUID, "Title", "Description", createTime, createTime)

    inboxRepo.insert(stuff)

    def makeMoveToTrash(userUUID: UUID, stuffUUID: UUID): MoveToTrash = new MoveToTrash(userUUID, stuffUUID)
    def makeMoveToTrash(timestamp: LocalDateTime): MoveToTrash = {
      generator.currentTime.setTime(timestamp)
      makeMoveToTrash(theUserUUID, theStuffUUID)
    }

  }

  def withFixture(test: OneArgTest): Outcome = {
    test(new TestFixture)
  }
}

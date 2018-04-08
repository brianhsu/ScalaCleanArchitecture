package unitTest.usecase.inbox

import java.time._
import java.util.UUID

import moe.brianhsu.gtd.entity._
import moe.brianhsu.gtd.journal.UpdateLog
import moe.brianhsu.gtd.repo.InboxRepo
import moe.brianhsu.gtd.repo.memory.InMemoryInboxRepo
import moe.brianhsu.gtd.usecase.inbox.MoveToTrash
import moe.brianhsu.gtd.validator._
import org.scalatest._
import unitTest.stub._

class MoveToTrashSpec extends fixture.WordSpec with Matchers with OptionValues {

  object LoggedInUser {

    val owner = User(
      UUID.fromString("2a53d868-0b7c-4058-b4fe-b727eb24da6f"),
      "owner@example.com", "UserName"
    )

    val other = User(
      UUID.fromString("bdc5a146-b5de-4969-93ca-6d43f0d98197"),
      "guest@example.com", "Guest"
    )
  }
  private val theStuffUUID = UUID.fromString("7a632665-46bf-4766-8404-c191a2637632")
  private val nonExistUUID = UUID.fromString("12345678-90ab-cdef-1234-567890abcdef")

  private val theUser = User(
    UUID.fromString("2a53d868-0b7c-4058-b4fe-b727eb24da6f"),
    "user@example.com", "UserName"
  )

  "MoveToTrash" when {
    "validate request" should {

      "return an ParamError when UUID is not found" in { fixture =>
        val moveToTrash = fixture.makeMoveToTrash(LoggedInUser.owner, nonExistUUID)
        val error = moveToTrash.validate().value.error

        error shouldBe ParamError(List(FieldError("uuid", NotFound)))
      }

      "return an ParamError when stuff is not match with user UUID" in { fixture =>

        val moveToTrash = fixture.makeMoveToTrash(LoggedInUser.other, theStuffUUID)
        val error = moveToTrash.validate().value.error

        error shouldBe ParamError(List(FieldError("user", AccessDenied)))
      }

      "return None when everything is OK" in { fixture =>
        val moveToTrash = fixture.makeMoveToTrash(LoggedInUser.owner, theStuffUUID)
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
        
        val stuffInDB = fixture.inboxRepo.read.find(theStuffUUID).value

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
    implicit val inboxRepo: InboxRepo = InMemoryInboxRepo.makeInMemoryInbox
    
    val createTime: LocalDateTime = LocalDateTime.parse("2018-01-01T10:00:00")
    val stuff = Stuff(theStuffUUID, LoggedInUser.owner.uuid, "Title", "Description", createTime, createTime)

    inboxRepo.write.insert(stuff)

    def makeMoveToTrash(loggedInUser: User, stuffUUID: UUID): MoveToTrash = new MoveToTrash(loggedInUser, stuffUUID)
    def makeMoveToTrash(timestamp: LocalDateTime): MoveToTrash = {
      generator.currentTime.setTime(timestamp)
      makeMoveToTrash(LoggedInUser.owner, theStuffUUID)
    }

  }

  def withFixture(test: OneArgTest): Outcome = {
    test(new TestFixture)
  }
}

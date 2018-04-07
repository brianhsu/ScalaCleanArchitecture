package moe.brianhsu.gtd.usecase

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff
import moe.brianhsu.gtd.repo._
import moe.brianhsu.gtd.generator._
import moe.brianhsu.gtd.journal._
import moe.brianhsu.gtd.validator._

class MoveToTrash(userUUID: UUID, stuffUUID: UUID)
                 (implicit private val inboxRepo: InboxRepo, 
                  implicit private val generator: DynamicDataGenerator) extends UseCase[Stuff] {

  private val updateTime = generator.currentTime.time
  private lazy val updatedStuff = {
    inboxRepo.find(stuffUUID)
      .get
      .copy(isTrash = true, updateTime = this.updateTime)
  }

  def execute(): Stuff = {
    inboxRepo.update(stuffUUID, updatedStuff)
    updatedStuff
  }


  def validate(): Option[ValidationError] = {
    import ParamValidator._

    def isStuffExists(stuffUUID: UUID) = if (inboxRepo.find(stuffUUID).isDefined) None else Some(NotFound)
    def isUserMatched(stuffUUID: UUID)(userUUID: UUID) = inboxRepo.find(stuffUUID).filter(_.userUUID != userUUID).map(_ => AccessDenied)

    checkParams(
      forField("uuid", stuffUUID, isStuffExists),
      forField("userUUID", userUUID, isUserMatched(stuffUUID))
    )
  }

  def journal: Option[Journal] = Some(UpdateLog("inbox", stuffUUID, updatedStuff, updateTime))

}

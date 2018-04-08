package moe.brianhsu.gtd.usecase.inbox

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff
import moe.brianhsu.gtd.generator.DynamicDataGenerator
import moe.brianhsu.gtd.journal.InsertLog
import moe.brianhsu.gtd.repo.InboxRepo
import moe.brianhsu.gtd.usecase._
import moe.brianhsu.gtd.validator._

object CreateStuff {
  case class Request(userUUID: UUID, title: String, description: String)
}

class CreateStuff(request: CreateStuff.Request)
                 (implicit private val inboxRepo: InboxRepo, implicit private val generator: DynamicDataGenerator) 
                 extends UseCase[Stuff] {

  private lazy val uuid = generator.randomUUID.uuid
  private lazy val nowTime = generator.currentTime.time
  private lazy val stuff = Stuff(uuid, request.userUUID, request.title, request.description, nowTime, nowTime)

  override def validate(): Option[ValidationError] = {
    import ParamValidator._
    checkParams(
      forField("title", request.title, NonEmptyString),
      forField("description", request.description, NonEmptyString)
    )
  }

  override def execute(): Stuff = {
    inboxRepo.write.insert(stuff)
    stuff
  }

  override def journal = Some(InsertLog("inbox", stuff.uuid, stuff, nowTime))
}

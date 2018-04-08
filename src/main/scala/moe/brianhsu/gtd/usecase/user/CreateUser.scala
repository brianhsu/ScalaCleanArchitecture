package moe.brianhsu.gtd.usecase.user

import moe.brianhsu.gtd.entity.User
import moe.brianhsu.gtd.generator._
import moe.brianhsu.gtd.journal.InsertLog
import moe.brianhsu.gtd.repo.UserRepo
import moe.brianhsu.gtd.usecase._
import moe.brianhsu.gtd.validator._

object CreateUser {
  case class Request(email: String, name: String)
}


class CreateUser(request: CreateUser.Request)
                (implicit val userRepo: UserRepo, implicit val generator: DynamicDataGenerator) extends UseCase[User] {

  private lazy val uuid = generator.randomUUID.uuid
  private lazy val nowTime = generator.currentTime.time
  private lazy val user = User(uuid, request.email, request.name, nowTime, nowTime)

  override def execute(): User = {
    userRepo.write.insert(user)
    user
  }

  override def journal = Some(InsertLog("user", user.uuid, user, nowTime))

  override def validate(): Option[ValidationError] = {

    import ParamValidator._

    val isEmailDuplicated = (email: String) => userRepo.read.find(email).map(_ => IsDuplicated)

    checkParams(
      forField("email", request.email, NonEmptyString, EmailValidator, isEmailDuplicated),
      forField("name", request.name, ParamValidator.NonEmptyString)
    )
  }


}

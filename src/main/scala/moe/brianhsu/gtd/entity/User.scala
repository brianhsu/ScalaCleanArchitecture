package moe.brianhsu.gtd.entity

import java.time.LocalDateTime
import java.util.UUID

case class User(uuid: UUID, email: String, name: String, createTime: LocalDateTime, updateTime: LocalDateTime) extends Entity

object User {
  def apply(uuid: UUID, email: String, name: String): User = User(uuid, email, name, LocalDateTime.now(), LocalDateTime.now())
}

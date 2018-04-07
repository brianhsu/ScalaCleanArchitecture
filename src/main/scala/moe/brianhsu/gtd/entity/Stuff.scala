package moe.brianhsu.gtd.entity

import java.time.LocalDateTime
import java.util.UUID

case class Stuff(uuid: UUID, userUUID: UUID, title: String, description: String, createTime: LocalDateTime, updateTime: LocalDateTime, isTrash: Boolean = false) extends Entity

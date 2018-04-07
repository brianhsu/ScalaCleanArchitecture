package moe.brianhsu.gtd.journal

import java.time.LocalDateTime
import java.util.UUID

import moe.brianhsu.gtd.entity.Entity

case class InsertLog(entityType: String, uuid: UUID, entry: Entity, timestamp: LocalDateTime) extends Journal
case class UpdateLog(entityType: String, uuid: UUID, entry: Entity, timestamp: LocalDateTime) extends Journal

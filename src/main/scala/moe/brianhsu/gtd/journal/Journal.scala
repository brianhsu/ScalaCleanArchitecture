package moe.brianhsu.gtd.journal

import java.time.LocalDateTime
import java.util.UUID

import moe.brianhsu.gtd.entity.Entity

sealed trait Journal

case object DoNothing extends Journal
case class InsertLog(entityType: String, uuid: UUID, entry: Entity, timestamp: LocalDateTime) extends Journal
case class UpdateLog(entityType: String, uuid: UUID, entry: Entity, timestamp: LocalDateTime) extends Journal

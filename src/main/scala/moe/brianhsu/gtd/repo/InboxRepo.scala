package moe.brianhsu.gtd.repo

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff

trait InboxRepo {
  def find(uuid: UUID): Option[Stuff]
  def insert(stuff: Stuff): Unit
}

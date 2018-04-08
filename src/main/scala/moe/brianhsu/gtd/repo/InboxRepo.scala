package moe.brianhsu.gtd.repo

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff

trait InboxReadable {
  def find(uuid: UUID): Option[Stuff]
}

trait InboxWritable {
  def insert(stuff: Stuff): Unit
  def update(uuid: UUID, stuff: Stuff): Unit
}

case class InboxRepo(read: InboxReadable, write: InboxWritable)

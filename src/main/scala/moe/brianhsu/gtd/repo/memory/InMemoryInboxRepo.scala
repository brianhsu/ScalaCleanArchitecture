package moe.brianhsu.gtd.repo.memory

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff
import moe.brianhsu.gtd.repo.InboxRepo

class InMemoryInboxRepo extends InboxRepo {
  private var stuffList: List[Stuff] = Nil

  def find(uuid: UUID) = stuffList.find(_.uuid == uuid).headOption
  def insert(stuff: Stuff) = {
    stuffList ::= stuff
  }
}

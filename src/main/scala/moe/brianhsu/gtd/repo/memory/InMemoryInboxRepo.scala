package moe.brianhsu.gtd.repo.memory

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff
import moe.brianhsu.gtd.repo.InboxRepo

class InMemoryInboxRepo extends InboxRepo {

  private var stuffList: List[Stuff] = Nil

  def find(uuid: UUID): Option[Stuff] = stuffList.find(_.uuid == uuid)

  def insert(stuff: Stuff): Unit = {
    stuffList ::= stuff
  }

  def update(uuid: UUID, stuff: Stuff): Unit = {
    stuffList = stuff :: stuffList.filterNot(_.uuid == uuid)
  }

}

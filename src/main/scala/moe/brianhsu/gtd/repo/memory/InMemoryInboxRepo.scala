package moe.brianhsu.gtd.repo.memory

import java.util.UUID

import moe.brianhsu.gtd.entity.Stuff
import moe.brianhsu.gtd.repo._

object InMemoryInboxRepo {

  def makeInMemoryInbox = {
    val inbox = new InMemoryInboxReadable with InMemoryInboxWritable
    InboxRepo(inbox, inbox)
  }

  private trait InMemoryInboxRepo {
    protected var stuffList: List[Stuff] = Nil
  }

  private trait InMemoryInboxReadable extends InMemoryInboxRepo with InboxReadable {
    def find(uuid: UUID): Option[Stuff] = stuffList.find(_.uuid == uuid)
  }

  private trait InMemoryInboxWritable extends InMemoryInboxRepo with InboxWritable {

    def insert(stuff: Stuff): Unit = {
      stuffList ::= stuff
    }

    def update(uuid: UUID, stuff: Stuff): Unit = {
      stuffList = stuff :: stuffList.filterNot(_.uuid == uuid)
    }
  }
}

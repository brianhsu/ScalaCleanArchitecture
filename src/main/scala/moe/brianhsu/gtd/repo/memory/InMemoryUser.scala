package moe.brianhsu.gtd.repo.memory

import moe.brianhsu.gtd.entity.User
import moe.brianhsu.gtd.repo._

object InMemoryUser {

  def makeInMemoryUser: UserRepo = {
    val inMemory = new InMemoryUserReadable with InMemoryUserWritable
    UserRepo(inMemory, inMemory)
  }

  trait InMemoryUserBase {
    protected var userList: List[User] = Nil
  }

  trait InMemoryUserReadable extends InMemoryUserBase with UserReadable {
    override def find(email: String): Option[User] = userList.find(_.email == email)
  }

  trait InMemoryUserWritable extends InMemoryUserBase with UserWritable {
    override def insert(user: User): Unit = {
      userList ::= user
    }
  }

}
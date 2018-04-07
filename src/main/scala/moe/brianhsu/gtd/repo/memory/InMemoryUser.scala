package moe.brianhsu.gtd.repo.memory

import moe.brianhsu.gtd.entity.User
import moe.brianhsu.gtd.repo.UserRepo

class InMemoryUser extends UserRepo {
  var userList: List[User] = Nil

  override def insert(user: User): Unit = {
    userList ::= user
  }

  override def find(email: String): Option[User] = userList.find(_.email == email)
}
package moe.brianhsu.gtd.repo

import moe.brianhsu.gtd.entity.User

trait UserRepo {
  def insert(user: User): Unit
  def find(email: String): Option[User]
}

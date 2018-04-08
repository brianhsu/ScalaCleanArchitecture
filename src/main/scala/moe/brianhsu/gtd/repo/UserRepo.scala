package moe.brianhsu.gtd.repo

import moe.brianhsu.gtd.entity.User

trait UserReadable {
  def find(email: String): Option[User]
}

trait UserWritable {
  def insert(user: User): Unit
}

case class UserRepo(read: UserReadable, write: UserWritable)

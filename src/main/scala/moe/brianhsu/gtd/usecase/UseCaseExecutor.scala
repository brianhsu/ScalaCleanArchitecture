package moe.brianhsu.gtd.usecase

import moe.brianhsu.gtd.journal.Journal

import scala.util.Try

abstract class UseCaseExecutor {
  def execute[T](useCase: UseCase[T]): Try[T] = {
    Try {
      useCase.validate().foreach { error => throw error }
      val result = useCase.execute()
      useCase.journal.foreach(appendJournal)
      result
    }
  }

  def appendJournal(journal: Journal): Unit
}


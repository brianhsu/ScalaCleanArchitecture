package moe.brianhsu.gtd.usecase

import moe.brianhsu.gtd.journal.Journal

import scala.util._

object UseCaseExecutor {
  type Presenter[T] = (Try[T] => Unit)
}

abstract class UseCaseExecutor {

  import UseCaseExecutor.Presenter

  def execute[T](useCase: UseCase[T])(presenter: Presenter[T]): Unit = {

    val result = Try {
      useCase.validate().foreach { error => throw error }
      val result = useCase.execute()
      useCase.journal.foreach(appendJournal)
      result
    }

    presenter(result)
  }


  def appendJournal(journal: Journal): Unit
}


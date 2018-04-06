package moe.brianhsu.gtd.usecase

import moe.brianhsu.gtd.journal.Journal
import moe.brianhsu.gtd.validator._

trait UseCase[T] {
  def execute(): T
  def validate(): Option[ValidationError]
  def journal: Option[Journal]
}

trait BaseUseCase[T] extends UseCase[T] {
  override def validate(): Option[ValidationError] = None
  override def journal: Option[Journal] = None
}


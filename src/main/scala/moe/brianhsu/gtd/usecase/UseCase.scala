package moe.brianhsu.gtd.usecase

trait UseCase[T] {
  def execute(): T
  def validate(): Option[ValidationError]
  def journal: Option[Journal]
}

trait BaseUseCase[T] extends UseCase[T] {
  override def validate(): Option[ValidationError] = None
  override def journal: Option[Journal] = None
}


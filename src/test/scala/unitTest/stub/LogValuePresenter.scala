package unitTest.stub

import scala.util._

import moe.brianhsu.gtd.usecase.UseCaseExecutor._

class LogValuePresenter[T] extends Presenter[T] {

  var isCalled = false
  private var value: Try[T] = Failure(new NoSuchElementException)

  def getValue: Try[T] = value

  override def apply(result: Try[T]): Unit = {
    this.isCalled = true
    this.value = result
  }
}

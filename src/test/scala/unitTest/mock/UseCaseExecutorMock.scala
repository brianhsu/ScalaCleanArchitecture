package unitTest.mock

import moe.brianhsu.gtd.usecase._

class UseCaseExecutorMock extends UseCaseExecutor {
  var journals: List[Journal] = Nil

  def appendJournal(journal: Journal) {
    this.journals ::= journal
  }
}


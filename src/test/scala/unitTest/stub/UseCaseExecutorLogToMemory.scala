package unitTest.stub

import moe.brianhsu.gtd.journal.Journal
import moe.brianhsu.gtd.usecase._

class UseCaseExecutorLogToMemory extends UseCaseExecutor {
  var journals: List[Journal] = Nil

  def appendJournal(journal: Journal) {
    this.journals ::= journal
  }
}


package unitTest.stub

import java.time.LocalDateTime

import moe.brianhsu.gtd.generator.CurrentTimeGenerator

class FakeCurrentTime extends CurrentTimeGenerator {
  private var mCurrentTime: LocalDateTime = null
  override def currentTime: LocalDateTime = mCurrentTime
  def setTime(time: LocalDateTime) {
    this.mCurrentTime = time
  }
}

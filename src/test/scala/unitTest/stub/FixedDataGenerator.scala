package unitTest.stub

import java.time.LocalDateTime
import java.util.UUID

import moe.brianhsu.gtd.generator._

class FixedDataGenerator extends DynamicDataGenerator {

  override val randomUUID = new FixedUUIDGenerator
  override val currentTime = new FixedTimeGenerator

  class FixedTimeGenerator extends CurrentTimeGenerator {
    private var currentTime: LocalDateTime = null

    override def time: LocalDateTime = currentTime

    def setTime(time: LocalDateTime) {
      this.currentTime = time
    }
  }
  
  
  class FixedUUIDGenerator extends UUIDGenerator {

    private var currentUUID = UUID.fromString("ec0509a2-bb89-41a4-a8bb-b56816f61890")

    override def uuid = currentUUID

    def setUUID(uuid: String) = {
      this.currentUUID = UUID.fromString(uuid)
    }
  }
}

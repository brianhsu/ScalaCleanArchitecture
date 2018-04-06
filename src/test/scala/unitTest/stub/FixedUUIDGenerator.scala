package unitTest.stub

import java.util.UUID

import moe.brianhsu.gtd.generator.UUIDGenerator

class FixedUUIDGenerator extends UUIDGenerator {
  private var uuid = UUID.fromString("ec0509a2-bb89-41a4-a8bb-b56816f61890")
  def randomUUID = uuid
  def setUUID(uuid: String) = {
    this.uuid = UUID.fromString(uuid)
  }
}

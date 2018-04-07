package moe.brianhsu.gtd.generator

trait DynamicDataGenerator {
  def randomUUID: UUIDGenerator
  def currentTime: CurrentTimeGenerator
}

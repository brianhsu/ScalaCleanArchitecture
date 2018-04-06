package moe.brianhsu.gtd.generator

import java.time.LocalDateTime

trait CurrentTimeGenerator {
  def currentTime: LocalDateTime
}

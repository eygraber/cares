package com.eygraber.cure.samples.simple_clock.clock

data class ClockEntity(val time: String) {
  companion object {
    fun default() = ClockEntity("")
  }
}

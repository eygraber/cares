package com.eygraber.cure.samples.simple_clock.clock

data class ClockState(val time: String) {
  companion object {
    fun default() = ClockState("")
  }
}

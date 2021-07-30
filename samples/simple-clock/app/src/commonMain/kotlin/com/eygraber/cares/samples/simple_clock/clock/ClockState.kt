package com.eygraber.cares.samples.simple_clock.clock

import kotlinx.serialization.Serializable

@Serializable
data class ClockState(val time: String) {
  companion object {
    fun default() = ClockState("")
  }
}

package com.eygraber.cares.samples.simple_clock.clock

sealed class ClockEvent {
  object SettingsClicked : ClockEvent()
}

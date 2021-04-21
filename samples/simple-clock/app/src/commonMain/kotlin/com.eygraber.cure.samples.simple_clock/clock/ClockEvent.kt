package com.eygraber.cure.samples.simple_clock.clock

sealed class ClockEvent {
  object SettingsClicked : ClockEvent()
}

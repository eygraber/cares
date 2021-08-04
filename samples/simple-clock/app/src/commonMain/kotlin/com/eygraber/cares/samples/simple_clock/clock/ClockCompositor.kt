package com.eygraber.cares.samples.simple_clock.clock

import com.eygraber.cares.Compositor
import com.eygraber.cares.EventFlow
import kotlinx.coroutines.flow.map

class ClockCompositor : Compositor<ClockState, ClockEvent> {
  private val clockUseCase = ClockUseCase()

  override fun stateFlow(events: EventFlow<ClockEvent>) =
    clockUseCase
      .secondsTick()
      .map { ClockState(it) }
}

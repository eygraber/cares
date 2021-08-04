package com.eygraber.cares.samples.simple_clock.clock

import com.eygraber.cares.Compositor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClockCompositor : Compositor<ClockState, ClockEvent> {
  private val clockUseCase = ClockUseCase()

  override fun stateFlow(events: Flow<ClockEvent>) =
    clockUseCase
      .secondsTick()
      .map { ClockState(it) }
}

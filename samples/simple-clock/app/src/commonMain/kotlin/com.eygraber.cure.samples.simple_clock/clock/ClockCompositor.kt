package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.Compositor
import kotlinx.coroutines.flow.map

class ClockCompositor : Compositor<ClockState, ClockEvent> {
  private val clockUseCase = ClockUseCase()

  override val stateFlow =
    clockUseCase
      .secondsTick()
      .map { ClockState(it) }
}

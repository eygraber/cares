package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.Compositor
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.startWith

class ClockCompositor(
  private val currentState: StateFlow<ClockState>
) : Compositor<ClockState, ClockEvent> {
  private val clockUseCase = ClockUseCase()

  override val stateFlow =
    clockUseCase
      .secondsTick()
      .map { ClockState(it) }
      .onStart { emit(currentState.value) }
}

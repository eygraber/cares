package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.Compositor
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ClockCompositor : Compositor<ClockEntity> {
  private val clockUseCase = ClockUseCase()

  override val stateFlow =
    clockUseCase
      .secondsTick()
      .map { ClockEntity(it) }
}

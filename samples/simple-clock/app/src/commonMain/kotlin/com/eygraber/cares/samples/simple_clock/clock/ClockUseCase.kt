package com.eygraber.cares.samples.simple_clock.clock

import com.soywiz.klock.DateTimeTz
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class ClockUseCase {
  fun secondsTick() = flow {
    while(true) {
      emit(DateTimeTz.nowLocal().format("h:mm:ss a"))
      delay(1_000)
    }
  }
}

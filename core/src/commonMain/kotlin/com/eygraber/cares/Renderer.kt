package com.eygraber.cares

import androidx.compose.runtime.Composable
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

public abstract class Renderer<State, Event> {
  internal val eventEmitter: AtomicRef<((Event) -> Boolean)?> = atomic(null)

  protected fun emitEvent(event: Event) {
    eventEmitter.value?.invoke(event) ?: error("The emitter was never initialized")
  }

  @Composable
  public abstract fun render(state: State)
}

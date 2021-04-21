package com.eygraber.cure

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

public interface EventEmitter<Event> {
  public fun emitEvent(event: Event)
}

public fun <Event> eventEmitter(): EventEmitter<Event> = EventEmitterImpl()

internal class EventEmitterImpl<Event> : EventEmitter<Event> {
  private val emitter: AtomicRef<((Event) -> Boolean)?> = atomic(null)

  internal fun setEmitter(emitter: (Event) -> Boolean) {
    this.emitter.value = emitter
  }

  override fun emitEvent(event: Event) {
    emitter.value?.invoke(event) ?: error("The emitter was never initialized")
  }
}

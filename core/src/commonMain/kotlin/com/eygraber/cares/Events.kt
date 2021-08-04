package com.eygraber.cares

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

public interface EventEmitter<Event> {
  public fun emitEvent(event: Event): Boolean
  public operator fun invoke(event: Event): Boolean = emitEvent(event)
}

public interface EventFlow<Event> {
  public fun flow(): Flow<Event>
  public operator fun invoke(): Flow<Event> = flow()
}

public class Events<Event>(
  private val events: MutableSharedFlow<Event> = MutableSharedFlow()
) : EventEmitter<Event>, EventFlow<Event> {
  override fun emitEvent(event: Event): Boolean = events.tryEmit(event)

  override fun flow(): Flow<Event> = events
}

package com.eygraber.cares

import androidx.compose.runtime.Composable

public interface Renderer<State, Event> {
  @Composable
  public fun render(
    state: State,
    emitEvent: Emitter<Event>
  )
}

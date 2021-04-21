package com.eygraber.cure

import androidx.compose.runtime.Composable

public interface Renderer<State, Event> {
  @Composable
  public fun render(state: State, emitEvent: (Event) -> Boolean)
}

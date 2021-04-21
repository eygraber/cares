package com.eygraber.cure

import androidx.compose.runtime.Composable

public interface Renderer<State> {
  @Composable
  public fun render(state: State)
}

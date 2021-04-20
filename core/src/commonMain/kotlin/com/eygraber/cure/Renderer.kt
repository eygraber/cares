package com.eygraber.cure

import androidx.compose.runtime.Composable

public interface Renderer<E> {
  @Composable
  public fun render(state: E)
}

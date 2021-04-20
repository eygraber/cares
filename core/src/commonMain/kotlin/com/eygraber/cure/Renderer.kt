package com.eygraber.cure

import androidx.compose.runtime.Composable

interface Renderer<E> {
  @Composable
  fun render(state: E)
}

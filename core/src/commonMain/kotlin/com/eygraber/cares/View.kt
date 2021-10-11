package com.eygraber.cares

import androidx.compose.runtime.Composable

public interface View<State> {
  public val vm: VM<State>

  @Composable
  public fun render()
}

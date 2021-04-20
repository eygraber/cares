package com.eygraber.cure.samples.simple_clock

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.eygraber.cure.JsonStateSerializer
import com.eygraber.cure.samples.simple_clock.clock.ClockRenderNode
import com.eygraber.cure.window.RenderWindow

@OptIn(ExperimentalAnimationApi::class)
val renderWindow =
  RenderWindow(
    factoryKeySerializer = SimpleClockFactoryKey.serializer(),
    stateSerializer = JsonStateSerializer()
  ) { id ->
    when(id) {
      SimpleClockFactoryKey.Clock -> ClockRenderNode
    }
  }

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AppContent() {
  renderWindow.render()
}

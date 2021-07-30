package com.eygraber.cure.samples.simple_clock

import androidx.compose.runtime.Composable
import com.eygraber.cure.JsonStateSerializer
import com.eygraber.cure.samples.simple_clock.clock.ClockRenderNode
import com.eygraber.cure.samples.simple_clock.clock.ClockState
import com.eygraber.cure.window.RenderWindow

val renderWindow =
  RenderWindow(
    factoryKeySerializer = SimpleClockFactoryKey.serializer(),
    stateSerializer = JsonStateSerializer()
  ) { (key) ->
    when(key) {
      SimpleClockFactoryKey.Clock -> ClockRenderNode(initialState = ClockState.default())
    }
  }

@Composable
fun AppContent() {
  renderWindow.render()
}

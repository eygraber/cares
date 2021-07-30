package com.eygraber.cares.samples.simple_clock

import androidx.compose.runtime.Composable
import com.eygraber.cares.JsonStateSerializer
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.samples.simple_clock.clock.ClockRenderNode
import com.eygraber.cares.samples.simple_clock.clock.ClockState

val navWindow =
  NavWindow(
    factoryKeySerializer = SimpleClockFactoryKey.serializer(),
    stateSerializer = JsonStateSerializer()
  ) { (key) ->
    when(key) {
      SimpleClockFactoryKey.Clock -> ClockRenderNode(initialState = ClockState.default())
    }
  }

@Composable
fun AppContent() {
  navWindow.render()
}

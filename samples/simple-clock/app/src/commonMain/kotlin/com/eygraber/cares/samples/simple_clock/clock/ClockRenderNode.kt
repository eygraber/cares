package com.eygraber.cares.samples.simple_clock.clock

import com.eygraber.cares.RenderNode
import kotlinx.serialization.serializer

class ClockRenderNode(
  initialState: ClockState
) : RenderNode<ClockState, ClockEvent>(initialState) {
  override val compositor = ClockCompositor()
  override val renderer = ClockRenderer()

  override val serializer = serializer<ClockState>()
}

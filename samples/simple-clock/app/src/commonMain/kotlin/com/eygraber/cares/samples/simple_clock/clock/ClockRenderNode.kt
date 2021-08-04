package com.eygraber.cares.samples.simple_clock.clock

import com.eygraber.cares.RenderNode
import com.eygraber.cares.SerializableRenderNode
import kotlinx.serialization.serializer

class ClockRenderNode(
  initialState: ClockState
) : RenderNode<ClockState, ClockEvent, Unit>(initialState), SerializableRenderNode<ClockState> {
  override val compositor = ClockCompositor()
  override val renderer = ClockRenderer()

  override val serializer = serializer<ClockState>()
}

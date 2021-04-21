package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.RenderNode
import com.eygraber.cure.StateSerializer

class ClockRenderNode : RenderNode<ClockState, ClockEvent>() {
  override val initialState: ClockState = ClockState.default()
  override val compositor = ClockCompositor()
  override val renderer = ClockRenderer()

  companion object Factory : RenderNode.Factory<ClockState, ClockEvent> {
    override fun create(
      args: ByteArray?,
      savedState: ByteArray?,
      serializer: StateSerializer
    ) = ClockRenderNode()
  }
}

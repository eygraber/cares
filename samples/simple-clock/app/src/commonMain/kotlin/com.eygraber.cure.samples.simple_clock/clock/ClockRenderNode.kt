package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.RenderNode
import com.eygraber.cure.StateSerializer

class ClockRenderNode(
  initialState: ClockState
) : RenderNode<ClockState, ClockEvent>(initialState) {
  override val compositor = ClockCompositor(currentState)
  override val renderer = ClockRenderer()

  companion object Factory : RenderNode.Factory<ClockState, ClockEvent> {
    override fun create(
      args: ByteArray?,
      savedState: ByteArray?,
      serializer: StateSerializer
    ) = ClockRenderNode(
      initialState = ClockState.default()
    )
  }
}

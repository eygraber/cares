package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.RenderNode

class ClockRenderNode(
  initialState: ClockState
) : RenderNode<ClockState, ClockEvent>(initialState) {
  override val compositor = ClockCompositor()
  override val renderer = ClockRenderer()

  companion object Factory : RenderNode.Factory<ClockState, ClockEvent> {
    override fun create(
      args: RenderNode.Factory.SavedArgs?,
      savedState: RenderNode.Factory.SavedState?
    ) = ClockRenderNode(
      initialState = ClockState.default()
    )
  }
}

package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.RenderNode

class ClockRenderNode(
  initialState: ClockState
) : RenderNode<ClockState, ClockEvent>(initialState) {
  override val compositor = ClockCompositor()
  override val renderer = ClockRenderer()
}

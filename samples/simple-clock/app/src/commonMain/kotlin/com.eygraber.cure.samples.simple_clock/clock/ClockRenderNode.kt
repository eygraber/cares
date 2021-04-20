package com.eygraber.cure.samples.simple_clock.clock

import com.eygraber.cure.RenderNode
import com.eygraber.cure.StateSerializer

class ClockRenderNode : RenderNode<ClockEntity>() {
  override val initialState: ClockEntity = ClockEntity.default()
  override val compositor = ClockCompositor()
  override val renderer = ClockRenderer()

  companion object Factory : RenderNode.Factory<ClockEntity> {
    override fun create(
      args: ByteArray?,
      savedState: ByteArray?,
      serializer: StateSerializer
    ) = ClockRenderNode()
  }
}

package com.eygraber.cares.samples.torrents.footer

import com.eygraber.cares.Compositor
import com.eygraber.cares.RenderNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class FooterState(
  val text: String
)

class FooterRenderNode : RenderNode<FooterState, Unit>(
  initialState = FooterState("Not Connected")
) {
  override val renderer = FooterRenderer()
  override val compositor = object : Compositor<FooterState, Unit> {
    override fun stateFlow(events: Flow<Unit>) = emptyFlow<FooterState>()
  }
}

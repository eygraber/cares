package com.eygraber.cares.samples.torrents.datatable

import com.eygraber.cares.Compositor
import com.eygraber.cares.RenderNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class DataTableRenderNode : RenderNode<Unit, Unit>(Unit) {
  override val renderer = DataTableRenderer()
  override val compositor = object : Compositor<Unit, Unit> {
    override fun stateFlow(events: Flow<Unit>) = emptyFlow<Unit>()
  }
}

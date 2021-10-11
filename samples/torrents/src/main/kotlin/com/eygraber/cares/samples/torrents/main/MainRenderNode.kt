package com.eygraber.cares.samples.torrents.main

import com.eygraber.cares.Compositor
import com.eygraber.cares.RenderNode
import com.eygraber.cares.samples.torrents.controls.ControlsRenderNode
import com.eygraber.cares.samples.torrents.datatable.DataTableRenderNode
import com.eygraber.cares.samples.torrents.footer.FooterRenderNode
import com.eygraber.cares.samples.torrents.stats.StatsRenderNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainRenderNode : RenderNode<Unit, Unit>(Unit) {
  override val renderer =
    MainRenderer(
      controlsRenderNode = ControlsRenderNode(),
      dataTableRenderNode = DataTableRenderNode(),
      statsRenderNode = StatsRenderNode(),
      footerRenderNode = FooterRenderNode()
    )

  override val compositor = object : Compositor<Unit, Unit> {
    override fun stateFlow(events: Flow<Unit>) = flowOf(Unit)
  }
}

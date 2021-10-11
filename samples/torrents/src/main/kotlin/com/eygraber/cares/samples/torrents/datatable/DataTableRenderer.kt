package com.eygraber.cares.samples.torrents.datatable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer

class DataTableRenderer : Renderer<Unit, Unit> {
  @Composable
  override fun render(state: Unit, emitEvent: Emitter<Unit>) {
    Box(
      modifier = Modifier
        .fillMaxSize()
    ) {
      Text("DataTable")
    }
  }
}

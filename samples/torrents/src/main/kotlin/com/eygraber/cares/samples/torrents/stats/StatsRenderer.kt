package com.eygraber.cares.samples.torrents.stats

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer

class StatsRenderer : Renderer<Unit, Unit> {
  @Composable
  override fun render(state: Unit, emitEvent: Emitter<Unit>) {
    var topPadding by remember { mutableStateOf(300.dp) }

    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
    ) {
      Card(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = topPadding)
          .draggable(
            state = rememberDraggableState { delta ->
              val newTopPadding = when {
                delta < 0 -> topPadding + delta.dp
                else -> topPadding + delta.dp
              }
              topPadding = newTopPadding.coerceIn(30.dp..maxHeight - 30.dp)
            },
            orientation = Orientation.Vertical
          )
      ) {
        Text("Stats")
      }
    }
  }
}

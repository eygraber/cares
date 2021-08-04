package com.eygraber.cares.samples.simple_clock.clock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eygraber.cares.EventEmitter
import com.eygraber.cares.Renderer
import com.eygraber.compose.colorpicker.ColorPicker

class ClockRenderer : Renderer<ClockState, ClockEvent> {
  @Composable
  override fun render(state: ClockState, emitEvent: EventEmitter<ClockEvent>) {
    Card(
      shape = MaterialTheme.shapes.small.copy(all = CornerSize(8.dp)),
      elevation = 4.dp,
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      val defaultTextColor = MaterialTheme.colors.onSurface
      var textColor by remember { mutableStateOf(defaultTextColor) }

      Box(
        modifier = Modifier.padding(16.dp)
      ) {
        Column(
          modifier = Modifier.align(Alignment.Center),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = state.time,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier.weight(.20F)
          )

          ColorPicker(
            modifier = Modifier.weight(.80F)
          ) { newColor ->
            textColor = newColor
          }
        }

        Icon(
          imageVector = Icons.Rounded.Settings,
          contentDescription = "Settings",
          modifier = Modifier.align(Alignment.TopEnd).clickable {
            emitEvent(ClockEvent.SettingsClicked)
          }
        )
      }
    }
  }
}

package com.eygraber.cares.samples.torrents.footer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer

class FooterRenderer : Renderer<FooterState, Unit> {
  @Composable
  override fun render(state: FooterState, emitEvent: Emitter<Unit>) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Icon(
        Icons.Filled.SettingsEthernet,
        "Network State"
      )

      Text(
        state.text,
        fontSize = 10.sp,
        modifier = Modifier
          .alpha(0.7F)
          .padding(start = 8.dp)
          .align(Alignment.CenterVertically)
      )
    }
  }
}

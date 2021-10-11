package com.eygraber.cares.samples.torrents.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer

class ControlsRenderer : Renderer<ControlsState, ControlsEvents> {
  @Composable
  override fun render(state: ControlsState, emitEvent: Emitter<ControlsEvents>) {
    Row {
      ControlIcon(
        currentState = state.selectedIcon,
        thisState = ControlsIcons.Add,
        icon = Icons.Filled.Add,
        contentDescription = "Add torrent"
      ) {
        emitEvent(ControlsEvents.AddClicked)
      }

      ControlIcon(
        currentState = state.selectedIcon,
        thisState = ControlsIcons.Remove,
        icon = Icons.Filled.Remove,
        contentDescription = "Remove torrent"
      ) {
        emitEvent(ControlsEvents.RemoveClicked)
      }

      ControlIcon(
        currentState = state.selectedIcon,
        thisState = ControlsIcons.Filter,
        icon = Icons.Filled.Search,
        contentDescription = "Filter torrents by name"
      ) {
        emitEvent(ControlsEvents.FilterClicked)
      }

      ControlIcon(
        currentState = state.selectedIcon,
        thisState = ControlsIcons.Pause,
        icon = Icons.Filled.Pause,
        contentDescription = "Pause the selected torrents"
      ) {
        emitEvent(ControlsEvents.PauseClicked)
      }

      ControlIcon(
        currentState = state.selectedIcon,
        thisState = ControlsIcons.Start,
        icon = Icons.Filled.PlayArrow,
        contentDescription = "Resume the selected torrents"
      ) {
        emitEvent(ControlsEvents.StartClicked)
      }
    }
  }

  @Composable
  private fun ControlIcon(
    currentState: ControlsIcons,
    thisState: ControlsIcons,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
  ) {
    IconButton(
      onClick = onClick,
      modifier = Modifier.borderIfSelected(
        currentState = currentState,
        expectedState = thisState
      )
    ) {
      Icon(icon, contentDescription, modifier = Modifier.padding(6.dp))
    }
  }

  private fun Modifier.borderIfSelected(
    currentState: ControlsIcons,
    expectedState: ControlsIcons
  ) = composed {
    if(currentState == expectedState) {
      border(
        border = BorderStroke(width = 1.dp, MaterialTheme.colors.onBackground),
        shape = CircleShape
      )
    }
    else {
      this
    }
  }
}

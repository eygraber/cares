package com.eygraber.cares.samples.torrents.controls

import com.eygraber.cares.Compositor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ControlsCompositor : Compositor<ControlsState, ControlsEvents> {
  override fun stateFlow(events: Flow<ControlsEvents>) =
    events
      .map {
        ControlsState(
          when(it) {
            ControlsEvents.AddClicked -> ControlsIcons.Add
            ControlsEvents.RemoveClicked -> ControlsIcons.Remove
            ControlsEvents.FilterClicked -> ControlsIcons.Filter
            ControlsEvents.PauseClicked -> ControlsIcons.Pause
            ControlsEvents.StartClicked -> ControlsIcons.Start
          }
        )
      }
}

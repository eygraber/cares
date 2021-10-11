package com.eygraber.cares.samples.torrents.controls

import com.eygraber.cares.RenderNode

data class ControlsState(
  val selectedIcon: ControlsIcons
)

enum class ControlsIcons {
  Add,
  Remove,
  Filter,
  Pause,
  Start,
  None
}

enum class ControlsEvents {
  AddClicked,
  RemoveClicked,
  FilterClicked,
  PauseClicked,
  StartClicked
}

class ControlsRenderNode : RenderNode<ControlsState, ControlsEvents>(
  initialState = ControlsState(selectedIcon = ControlsIcons.None)
) {
  override val renderer = ControlsRenderer()
  override val compositor = ControlsCompositor()
}

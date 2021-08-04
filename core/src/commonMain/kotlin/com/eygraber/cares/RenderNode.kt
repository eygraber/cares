package com.eygraber.cares

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public abstract class RenderNode<State, Event>(
  initialState: State,
  private val events: Events<Event> = defaultEvents()
) {
  protected abstract val renderer: Renderer<State, Event>
  protected abstract val compositor: Compositor<State, Event>

  private val state: StateFlow<State> = MutableStateFlow(initialState)

  public fun currentState(): State = state.value

  @Composable
  public fun render() {
    val flow = remember { compositor.stateFlow(events) }
    val data by flow.collectAsState(state.value)
    (state as MutableStateFlow).value = data
    renderer.render(data, events)
  }

  public companion object {
    private fun <Event> defaultEvents(): Events<Event> = Events(
      events = MutableSharedFlow(extraBufferCapacity = 8)
    )
  }
}

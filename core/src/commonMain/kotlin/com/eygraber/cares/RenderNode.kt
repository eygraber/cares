package com.eygraber.cares

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

public abstract class RenderNode<State, Event>(
  initialState: State
) {
  protected abstract val renderer: Renderer<State, Event>
  protected abstract val compositor: Compositor<State, Event>

  private val stateFlow: StateFlow<State> = MutableStateFlow(initialState)

  private val events: EmitterConsumer<Event> by lazy {
    EmitterConsumer(
      flow = createEventsFlow()
    )
  }

  public fun currentState(): State = stateFlow.value

  protected open fun onEvent(event: Event) {}

  @Composable
  public fun render() {
    LaunchedEffect(this) {
      launch {
        events().collect { event ->
          onEvent(event)
        }
      }
    }

    val flow = remember { compositor.stateFlow(events()) }
    val state by flow.collectAsState(stateFlow.value)
    (stateFlow as MutableStateFlow).value = state
    renderer.render(state, events)
  }

  protected open fun createEventsFlow(): MutableSharedFlow<Event> =
    MutableSharedFlow(extraBufferCapacity = 8)
}

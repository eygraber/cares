package com.eygraber.cares

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

public abstract class RenderNode<State, Event>(
  initialState: State
) {
  protected abstract val renderer: Renderer<State, Event>
  protected abstract val compositor: Compositor<State, Event>

  protected val state: StateFlow<State> = MutableStateFlow(initialState)

  private val eventFlow: SharedFlow<Event> by lazy(::createEventFlow)

  private val eventEmitter: (Event) -> Boolean by lazy {
    { event: Event ->
      (eventFlow as MutableSharedFlow<Event>).tryEmit(event)
    }
  }

  public fun latestState(): State = state.value

  @Composable
  public fun render() {
    val data by compositor.stateFlow.collectAsState(state.value)
    (state as MutableStateFlow).value = data
    renderer.eventEmitter.compareAndSet(null, eventEmitter)
    renderer.render(data)
  }

  protected open fun createEventFlow(): MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 8)
}

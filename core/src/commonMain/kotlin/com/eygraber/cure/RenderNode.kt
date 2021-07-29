package com.eygraber.cure

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

public abstract class RenderNode<State, Event>(
  initialState: State
) {
  protected abstract val renderer: Renderer<State, Event>
  protected abstract val compositor: Compositor<State, Event>

  protected val currentState: StateFlow<State> = MutableStateFlow(initialState)

  private val eventFlow: SharedFlow<Event> by lazy(::createEventFlow)

  private val eventEmitter: (Event) -> Boolean by lazy {
    { event: Event ->
      (eventFlow as MutableSharedFlow<Event>).tryEmit(event)
    }
  }

  @Composable
  public fun render() {
    val data by compositor.stateFlow.collectAsState(currentState.value)
    (currentState as MutableStateFlow).value = data
    renderer.eventEmitter.compareAndSet(null, eventEmitter)
    renderer.render(data)
  }

  protected open val serializer: KSerializer<State>? = null

  /**
   * Serializes the value from [currentState] to a [ByteArray] using [stateSerializer].
   *
   * Returns null if either the value from [currentState] or [serializer] is null.
   */
  public fun serializeCurrentState(stateSerializer: StateSerializer): ByteArray? =
    currentState.value?.let { currentState ->
      serializer?.let { serializer ->
        stateSerializer.serialize(currentState, serializer)
      }
    }

  protected open fun createEventFlow(): MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 8)

  public class SavedArgs(
    @PublishedApi internal val data: ByteArray,
    @PublishedApi internal val serializer: StateSerializer
  ) {
    public inline fun <reified Arg> args(): Arg =
      serializer.deserialize(data, serializer())
  }

  public class SavedState(
    @PublishedApi internal val data: ByteArray,
    @PublishedApi internal val serializer: StateSerializer
  ) {
    public inline fun <reified Arg> state(): Arg =
      serializer.deserialize(data, serializer())
  }
}

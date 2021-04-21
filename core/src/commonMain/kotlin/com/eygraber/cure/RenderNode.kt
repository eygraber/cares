package com.eygraber.cure

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.KSerializer

public abstract class RenderNode<State, Event> {
  protected abstract val renderer: Renderer<State, Event>
  protected abstract val compositor: Compositor<State, Event>

  protected abstract val initialState: State

  private val currentState: AtomicRef<State?> = atomic(null as State?)

  protected open val eventFlow: SharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 8)

  @ExperimentalAnimationApi
  public open val transitions: Transitions? = Transitions(
    enter = slideInHorizontally(
      initialOffsetX = { it * 2 }
    ),
    exit = slideOutHorizontally(
      targetOffsetX = { -it }
    ),
    show = fadeIn(),
    hide = fadeOut()
  )

  @Composable internal fun render() {
    val data by compositor.stateFlow.collectAsState(initialState)
    currentState.value = data
    renderer.render(data) { event ->
      (eventFlow as MutableSharedFlow<Event>).tryEmit(event)
    }
  }

  protected open val serializer: KSerializer<State>? = null

  internal fun serialize(serializer: StateSerializer): ByteArray? = currentState.value?.let { currentState ->
    this.serializer?.let { entitySerializer ->
      serializer.serialize(currentState, entitySerializer)
    }
  }

  @ExperimentalAnimationApi
  public data class Transitions(
    val enter: EnterTransition,
    val exit: ExitTransition,
    val restoreFromBackstack: EnterTransition = enter,
    val sendToBackstack: ExitTransition = exit,
    val show: EnterTransition = enter,
    val hide: ExitTransition = exit
  ) {
    public fun getEnterAndExitTransitions(
      isShowOrHide: Boolean,
      isBackstackOp: Boolean
    ): Pair<EnterTransition, ExitTransition> = when {
      isShowOrHide -> show to hide

      isBackstackOp -> restoreFromBackstack to sendToBackstack

      else -> enter to exit
    }
  }

  public interface Factory<State, Event> {
    public fun create(
      args: ByteArray?,
      savedState: ByteArray?,
      serializer: StateSerializer
    ): RenderNode<State, Event>
  }
}

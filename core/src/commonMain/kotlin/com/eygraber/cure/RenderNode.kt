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
import kotlinx.serialization.KSerializer

abstract class RenderNode<E> {
  protected abstract val renderer: Renderer<E>
  protected abstract val compositor: Compositor<E>

  protected abstract val initialState: E

  private val currentState: AtomicRef<E?> = atomic(null as E?)

  @ExperimentalAnimationApi
  open val transitions: Transitions? = Transitions(
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
    renderer.render(data)
  }

  protected open val serializer: KSerializer<E>? = null

  internal fun serialize(serializer: StateSerializer): ByteArray? = currentState.value?.let { currentState ->
    this.serializer?.let { entitySerializer ->
      serializer.serialize(currentState, entitySerializer)
    }
  }

  @ExperimentalAnimationApi
  data class Transitions(
    val enter: EnterTransition,
    val exit: ExitTransition,
    val restoreFromBackstack: EnterTransition = enter,
    val sendToBackstack: ExitTransition = exit,
    val show: EnterTransition = enter,
    val hide: ExitTransition = exit
  ) {
    fun getEnterAndExitTransitions(
      isShowOrHide: Boolean,
      isBackstackOp: Boolean
    ) = when {
      isShowOrHide -> show to hide

      isBackstackOp -> restoreFromBackstack to sendToBackstack

      else -> enter to exit
    }
  }

  interface Factory<E> {
    fun create(
      args: ByteArray?,
      savedState: ByteArray?,
      serializer: StateSerializer
    ): RenderNode<E>
  }
}

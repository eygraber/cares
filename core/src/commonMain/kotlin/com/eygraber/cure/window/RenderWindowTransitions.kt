package com.eygraber.cure.window

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@ExperimentalAnimationApi
public data class RenderWindowTransitions(
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

  public companion object {
    public val Default: RenderWindowTransitions = RenderWindowTransitions(
      enter = slideInHorizontally(
        initialOffsetX = { it * 2 }
      ),
      exit = slideOutHorizontally(
        targetOffsetX = { -it }
      ),
      show = fadeIn(),
      hide = fadeOut()
    )
  }
}

public sealed interface RenderWindowTransitionOverride {
  public object NoTransition : RenderWindowTransitionOverride

  @ExperimentalAnimationApi
  public data class Transitions(
    val enter: EnterTransition,
    val exit: ExitTransition
  ) : RenderWindowTransitionOverride
}

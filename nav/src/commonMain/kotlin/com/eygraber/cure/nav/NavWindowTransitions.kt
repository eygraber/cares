package com.eygraber.cure.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@OptIn(ExperimentalAnimationApi::class)
public data class NavWindowTransitions(
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
    public val Default: NavWindowTransitions = NavWindowTransitions(
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

public sealed interface NavWindowTransition {
  public object NoTransition : NavWindowTransition

  @OptIn(ExperimentalAnimationApi::class)
  public data class Transitions(
    val enter: EnterTransition,
    val exit: ExitTransition
  ) : NavWindowTransition
}

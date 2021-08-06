package com.eygraber.cares.samples.nav.android.number

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.eygraber.cares.RenderNode
import com.eygraber.cares.SerializableRenderNode
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.nav.NavWindowTransition
import com.eygraber.cares.nav.updateWithBackstack
import com.eygraber.cares.samples.nav.android.NumberKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
data class NumberState(
  val display: Int,
  val isStackEmpty: Boolean
)

sealed interface NumberIntents {
  object Back : NumberIntents
  data class Next(val key: NumberKey) : NumberIntents
}

class NumberRenderNode(
  private val navWindow: NavWindow<NumberKey>,
  private val nextKey: NumberKey,
  initialState: NumberState
) : RenderNode<NumberState, Unit, NumberIntents>(initialState), SerializableRenderNode<NumberState> {
  override val renderer = NumberRenderer(nextKey)
  override val compositor = NumberCompositor(navWindow, initialState.display)

  override val serializer = serializer<NumberState>()

  @OptIn(ExperimentalAnimationApi::class)
  override fun onIntent(intent: NumberIntents) {
    navWindow.withBackstack {
      when(intent) {
        NumberIntents.Back -> if(size > 0) {
          popBackstack { _, _ ->
            NavWindowTransition.Transitions(
              enter = slideInHorizontally(
                initialOffsetX = { -it }
              ),
              exit = slideOutHorizontally(
                targetOffsetX = { it * 2 }
              )
            )
          }
        }

        is NumberIntents.Next -> updateWithBackstack(intent.key) {
          detach(
            when(nextKey) {
              NumberKey.One -> NumberKey.Five
              NumberKey.Two -> NumberKey.One
              NumberKey.Parent -> NumberKey.Two
              NumberKey.Four -> NumberKey.Parent
              NumberKey.Five -> NumberKey.Four
            }
          )
          add(intent.key)
        }
      }
    }
  }
}

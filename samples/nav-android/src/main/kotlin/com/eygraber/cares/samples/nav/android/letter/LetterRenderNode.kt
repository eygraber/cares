package com.eygraber.cares.samples.nav.android.letter

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
data class LetterState(
  val display: String,
  val showNext: Boolean,
  val isSiblingStackEmpty: Boolean,
  val isParentStackEmpty: Boolean
)

@Serializable
@Suppress("EnumNaming")
enum class LetterKey {
  A,
  B,
  C,
  D,
  E
}

sealed interface LetterIntents {
  object PreviousSibling : LetterIntents
  object Previous : LetterIntents
  data class NextSibling(val key: LetterKey) : LetterIntents
  data class Next(val key: NumberKey) : LetterIntents
}

class LetterRenderNode(
  private val parentNavWindow: NavWindow<NumberKey>,
  private val navWindow: NavWindow<LetterKey>,
  private val nextKey: NumberKey,
  private val nextSiblingKey: LetterKey,
  initialDisplay: String
) : RenderNode<LetterState, Unit, LetterIntents>(LetterState(initialDisplay, initialDisplay == "E", true, true)),
  SerializableRenderNode<LetterState> {
  override val renderer = LetterRenderer(nextSiblingKey, nextKey)
  override val compositor = LetterCompositor(parentNavWindow, navWindow, initialDisplay)

  override val serializer = serializer<LetterState>()

  @OptIn(ExperimentalAnimationApi::class)
  override fun onIntent(intent: LetterIntents) {
    when(intent) {
      LetterIntents.PreviousSibling -> navWindow.withBackstack {
        if(size > 0) {
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
      }

      LetterIntents.Previous -> parentNavWindow.withBackstack {
        if(size > 0) {
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
      }

      is LetterIntents.NextSibling -> navWindow.withBackstack {
        updateWithBackstack(intent.key) {
          detach(
            when(nextSiblingKey) {
              LetterKey.A -> LetterKey.E
              LetterKey.B -> LetterKey.A
              LetterKey.C -> LetterKey.B
              LetterKey.D -> LetterKey.C
              LetterKey.E -> LetterKey.D
            }
          )
          add(intent.key)
        }
      }

      is LetterIntents.Next -> parentNavWindow.withBackstack {
        updateWithBackstack(intent.key) {
          detach(NumberKey.Parent)
          add(intent.key)
        }
      }
    }
  }
}

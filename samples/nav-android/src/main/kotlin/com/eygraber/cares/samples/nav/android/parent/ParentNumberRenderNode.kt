package com.eygraber.cares.samples.nav.android.parent

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.eygraber.cares.JsonStateSerializer
import com.eygraber.cares.RenderNode
import com.eygraber.cares.SerializableRenderNode
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.nav.NavWindowParent
import com.eygraber.cares.nav.NavWindowTransition
import com.eygraber.cares.nav.updateWithBackstack
import com.eygraber.cares.samples.nav.android.NumberKey
import com.eygraber.cares.samples.nav.android.letter.LetterKey
import com.eygraber.cares.samples.nav.android.letter.LetterRenderNode
import kotlinx.serialization.serializer

sealed interface ParentNumberIntents {
  object Back : ParentNumberIntents
  data class Next(val key: NumberKey) : ParentNumberIntents
}

class ParentNumberRenderNode(
  private val navWindow: NavWindow<NumberKey>,
  private val nextKey: NumberKey
) : RenderNode<Unit, Unit, ParentNumberIntents>(Unit),
  SerializableRenderNode<Unit>,
  NavWindowParent<LetterKey> {
  override val childNavWindow = NavWindow<LetterKey>(
    factoryKeySerializer = serializer(),
    stateSerializer = JsonStateSerializer()
  ) { args ->
    when(args.key) {
      LetterKey.A -> LetterRenderNode(navWindow, this, nextKey, LetterKey.B, "A")
      LetterKey.B -> LetterRenderNode(navWindow, this, nextKey, LetterKey.C, "B")
      LetterKey.C -> LetterRenderNode(navWindow, this, nextKey, LetterKey.D, "C")
      LetterKey.D -> LetterRenderNode(navWindow, this, nextKey, LetterKey.E, "D")
      LetterKey.E -> LetterRenderNode(navWindow, this, nextKey, LetterKey.A, "E")
    }
  }.apply {
    update {
      add(LetterKey.A)
    }
  }

  override val renderer = ParentNumberRenderer(childNavWindow)
  override val compositor = ParentNumberCompositor()

  override val serializer = serializer<Unit>()

  @OptIn(ExperimentalAnimationApi::class)
  override fun onIntent(intent: ParentNumberIntents) {
    navWindow.withBackstack {
      when(intent) {
        ParentNumberIntents.Back -> if(size > 0) {
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

        is ParentNumberIntents.Next -> updateWithBackstack(intent.key) {
          detach(NumberKey.Parent)
          add(intent.key)
        }
      }
    }
  }
}

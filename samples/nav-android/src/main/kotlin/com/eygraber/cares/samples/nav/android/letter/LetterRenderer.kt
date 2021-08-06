package com.eygraber.cares.samples.nav.android.letter

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer
import com.eygraber.cares.samples.nav.android.NumberKey

class LetterRenderer(
  private val nextSiblingKey: LetterKey,
  private val nextKey: NumberKey
) : Renderer<LetterState, Unit, LetterIntents> {
  @Composable
  override fun render(
    state: LetterState,
    emitEvent: Emitter<Unit>,
    emitIntent: Emitter<LetterIntents>
  ) {
    if(!state.isSiblingStackEmpty || !state.isParentStackEmpty) {
      BackHandler {
        emitIntent(
          when {
            !state.isSiblingStackEmpty -> LetterIntents.PreviousSibling
            else -> LetterIntents.Previous
          }
        )
      }
    }

    Box {
      Column(
        modifier = Modifier.align(Alignment.Center)
      ) {
        Button(
          onClick = { emitIntent(LetterIntents.NextSibling(nextSiblingKey)) },
          modifier = Modifier
            .align(Alignment.CenterHorizontally),
        ) {
          Text(text = state.display)
        }

        if(state.showNext) {
          Button(
            onClick = { emitIntent(LetterIntents.Next(nextKey)) },
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .padding(top = 16.dp),
          ) {
            Text(text = "Next")
          }
        }
      }
    }
  }
}

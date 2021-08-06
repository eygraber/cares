package com.eygraber.cares.samples.nav.android.number

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer
import com.eygraber.cares.samples.nav.android.NumberKey

class NumberRenderer(
  private val nextKey: NumberKey
) : Renderer<NumberState, Unit, NumberIntents> {
  @Composable
  override fun render(
    state: NumberState,
    emitEvent: Emitter<Unit>,
    emitIntent: Emitter<NumberIntents>
  ) {
    if(!state.isStackEmpty) {
      BackHandler {
        emitIntent(NumberIntents.Back)
      }
    }

    Card(
      modifier = Modifier.fillMaxSize()
    ) {
      Box {
        Button(
          onClick = { emitIntent(NumberIntents.Next(nextKey)) },
          modifier = Modifier
            .align(Alignment.Center),
        ) {
          Text(text = state.display.toString())
        }
      }
    }
  }
}

package com.eygraber.cares.samples.nav.android.parent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.cares.Emitter
import com.eygraber.cares.Renderer
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.samples.nav.android.letter.LetterKey

class ParentNumberRenderer(
  private val childNavWindow: NavWindow<LetterKey>
) : Renderer<Unit, Unit, ParentNumberIntents> {
  @Composable
  override fun render(
    state: Unit,
    emitEvent: Emitter<Unit>,
    emitIntent: Emitter<ParentNumberIntents>
  ) {
    BackHandler {
      emitIntent(ParentNumberIntents.Back)
    }

    Card(
      modifier = Modifier.fillMaxSize()
    ) {
      childNavWindow.render()
    }
  }
}

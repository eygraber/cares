package com.eygraber.cares.samples.nav.android.letter

import com.eygraber.cares.Compositor
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.samples.nav.android.NumberKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class LetterCompositor(
  private val parentNavWindow: NavWindow<NumberKey>,
  private val navWindow: NavWindow<LetterKey>,
  private val display: String
) : Compositor<LetterState, Unit> {
  override fun stateFlow(events: Flow<Unit>) =
    combine(
      parentNavWindow.updates(),
      navWindow.updates()
    ) { _, _ ->
      parentNavWindow.withBackstack {
        size
      } to navWindow.withBackstack {
        size
      }
    }.map { (parentBackstackSize, backstackSize) ->
      LetterState(
        display = display,
        isParentStackEmpty = parentBackstackSize == 0,
        isSiblingStackEmpty = backstackSize == 0,
        showNext = display == "E"
      )
    }
}

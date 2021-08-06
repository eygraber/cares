package com.eygraber.cares.samples.nav.android.number

import com.eygraber.cares.Compositor
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.samples.nav.android.NumberKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NumberCompositor(
  private val navWindow: NavWindow<NumberKey>,
  private val display: Int
) : Compositor<NumberState, Unit> {
  override fun stateFlow(events: Flow<Unit>) =
    navWindow
      .updates()
      .map {
        navWindow.withBackstack {
          size
        }
      }
      .map { backstackSize ->
        NumberState(
          display = display,
          isStackEmpty = backstackSize == 0
        )
      }
}

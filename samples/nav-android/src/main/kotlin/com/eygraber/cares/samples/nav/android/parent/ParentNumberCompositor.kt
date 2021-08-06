package com.eygraber.cares.samples.nav.android.parent

import com.eygraber.cares.Compositor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ParentNumberCompositor : Compositor<Unit, Unit> {
  override fun stateFlow(events: Flow<Unit>) = emptyFlow<Unit>()
}

package com.eygraber.cares

import kotlinx.coroutines.flow.Flow

public interface Compositor<State, Event> {
  public val stateFlow: Flow<State>
}

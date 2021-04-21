package com.eygraber.cure

import kotlinx.coroutines.flow.Flow

public interface Compositor<State, Event> {
  public val stateFlow: Flow<State>
}

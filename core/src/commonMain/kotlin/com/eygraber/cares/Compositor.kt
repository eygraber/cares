package com.eygraber.cares

import kotlinx.coroutines.flow.Flow

public interface Compositor<State, Event> {
  public fun stateFlow(events: Flow<Event>): Flow<State>
}

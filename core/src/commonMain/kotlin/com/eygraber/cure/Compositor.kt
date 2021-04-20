package com.eygraber.cure

import kotlinx.coroutines.flow.Flow

public interface Compositor<E> {
  public val stateFlow: Flow<E>
}

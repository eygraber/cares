package com.eygraber.cure

import kotlinx.coroutines.flow.Flow

interface Compositor<E> {
  val stateFlow: Flow<E>
}

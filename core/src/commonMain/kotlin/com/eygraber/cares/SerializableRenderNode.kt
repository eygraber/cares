package com.eygraber.cares

import kotlinx.serialization.KSerializer

public interface SerializableRenderNode<State> {
  public val serializer: KSerializer<State>

  public fun currentState(): State

  /**
   * Serializes the value from [currentState] to a [ByteArray] using [stateSerializer].
   */
  public fun serializeLatestState(stateSerializer: StateSerializer): ByteArray? =
    stateSerializer.serialize(currentState(), serializer)
}

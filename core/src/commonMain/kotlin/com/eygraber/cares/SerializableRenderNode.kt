package com.eygraber.cares

import kotlinx.serialization.KSerializer

public interface SerializableRenderNode<State> {
  public val serializer: KSerializer<State>

  public fun latestState(): State

  /**
   * Serializes the value from [latestState] to a [ByteArray] using [stateSerializer].
   */
  public fun serializeLatestState(stateSerializer: StateSerializer): ByteArray? =
    stateSerializer.serialize(latestState(), serializer)
}

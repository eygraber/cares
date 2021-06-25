package com.eygraber.cure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

public interface StateSerializer {
  public fun <D> serialize(data: D, serializer: KSerializer<D>): ByteArray
  public fun <D> deserialize(data: ByteArray, deserializer: KSerializer<D>): D
}

public class JsonStateSerializer : StateSerializer {
  override fun <D> serialize(data: D, serializer: KSerializer<D>): ByteArray =
    Json.encodeToString(serializer, data).encodeToByteArray()

  override fun <D> deserialize(data: ByteArray, deserializer: KSerializer<D>): D =
    Json.decodeFromString(deserializer, data.decodeToString())
}

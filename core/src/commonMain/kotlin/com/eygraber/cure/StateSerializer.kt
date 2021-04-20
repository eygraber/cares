package com.eygraber.cure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface StateSerializer {
  fun <D> serialize(data: D, serializer: KSerializer<D>): ByteArray
  fun <D> deserialize(data: ByteArray, deserializer: KSerializer<D>): D
}

class JsonStateSerializer : StateSerializer {
  override fun <D> serialize(data: D, serializer: KSerializer<D>) =
      Json.encodeToString(serializer, data).encodeToByteArray()

  override fun <D> deserialize(data: ByteArray, deserializer: KSerializer<D>) =
      Json.decodeFromString(deserializer, data.decodeToString())
}

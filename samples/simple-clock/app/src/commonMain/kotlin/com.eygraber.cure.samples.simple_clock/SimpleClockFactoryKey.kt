package com.eygraber.cure.samples.simple_clock

import kotlinx.serialization.Serializable

@Serializable
sealed class SimpleClockFactoryKey {
  @Serializable
  object Clock : SimpleClockFactoryKey()
}

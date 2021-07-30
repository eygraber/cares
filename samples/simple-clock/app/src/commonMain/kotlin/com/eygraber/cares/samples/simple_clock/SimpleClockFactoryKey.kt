package com.eygraber.cares.samples.simple_clock

import kotlinx.serialization.Serializable

@Serializable
sealed class SimpleClockFactoryKey {
  @Serializable
  object Clock : SimpleClockFactoryKey()
}

package com.eygraber.cares

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

public interface Emitter<T> {
  public fun emit(item: T): Boolean
  public operator fun invoke(item: T): Boolean = emit(item)
}

internal interface Consumer<T> {
  fun flow(): Flow<T>
  operator fun invoke(): Flow<T> = flow()
}

internal class EmitterConsumer<T>(
  private val flow: MutableSharedFlow<T> = MutableSharedFlow()
) : Emitter<T>, Consumer<T> {
  override fun emit(item: T): Boolean = flow.tryEmit(item)

  override fun flow(): Flow<T> = flow
}

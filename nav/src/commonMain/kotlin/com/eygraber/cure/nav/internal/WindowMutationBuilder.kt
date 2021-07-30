package com.eygraber.cure.nav.internal

import com.eygraber.cure.StateSerializer
import com.eygraber.cure.nav.NavWindow
import kotlinx.serialization.KSerializer

internal class WindowMutationBuilder<FactoryKey>(
  private val stateSerializer: StateSerializer
) : NavWindow.UpdateBuilder<FactoryKey> {
  private val mutations = mutableListOf<WindowMutation<FactoryKey>>()

  override fun add(key: FactoryKey, isAttached: Boolean, isHidden: Boolean, id: String) {
    mutations += WindowMutation.Add(
      key = key,
      id = id,
      args = null,
      isAttached = isAttached,
      isHidden = isHidden
    )
  }

  override fun <T> add(
    key: FactoryKey,
    args: T,
    argsSerializer: KSerializer<T>,
    isAttached: Boolean,
    isHidden: Boolean,
    id: String
  ) {
    mutations += WindowMutation.Add(
      key = key,
      id = id,
      args = stateSerializer.serialize(args, argsSerializer),
      isAttached = isAttached,
      isHidden = isHidden
    )
  }

  override fun remove(key: FactoryKey, id: String) {
    mutations += WindowMutation.Remove(
      key = key,
      id = id
    )
  }

  override fun attach(key: FactoryKey, id: String, isHidden: Boolean) {
    mutations += WindowMutation.Attach(
      key = key,
      id = id,
      isHidden = isHidden,
      isBeingRestoredFromBackstack = false
    )
  }

  override fun detach(key: FactoryKey, id: String) {
    detach(key, id, isBeingSentToBackstack = false)
  }

  internal fun detach(key: FactoryKey, id: String, isBeingSentToBackstack: Boolean) {
    mutations += WindowMutation.Detach(
      key = key,
      id = id,
      isBeingSentToBackstack = isBeingSentToBackstack
    )
  }

  override fun show(key: FactoryKey, id: String) {
    mutations += WindowMutation.Show(
      key = key,
      id = id
    )
  }

  override fun hide(key: FactoryKey, id: String) {
    mutations += WindowMutation.Hide(
      key = key,
      id = id
    )
  }

  fun build() = mutations
}

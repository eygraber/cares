package com.eygraber.cares.portal.internal

import com.eygraber.cares.portal.PortalBackstack
import com.eygraber.cares.portal.PortalTransitions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PortalState<PortalKey>(
  private val defaultTransitions: PortalTransitions,
  private val doesIncompatibleStateThrow: Boolean
) {
  private val mutablePortalEntries = MutableStateFlow(emptyList<PortalEntry<PortalKey>>())
  private val mutableBackstackEntries = MutableStateFlow(emptyList<PortalBackstackEntry<PortalKey>>())

  private var transactionBuilder: PortalEntryBuilder<PortalKey>? = null

  inline val portalEntries: List<PortalEntry<PortalKey>> get() = mutablePortalEntries.value
  inline val backstackEntries: List<PortalBackstackEntry<PortalKey>> get() = mutableBackstackEntries.value

  fun portalEntriesUpdateFlow(): StateFlow<List<PortalEntry<PortalKey>>> = mutablePortalEntries
  fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<PortalKey>>> = mutableBackstackEntries

  fun startTransaction(backstack: PortalBackstack<PortalKey>) {
    // reentrant
    if(transactionBuilder != null) return

    transactionBuilder = PortalEntryBuilder(
      backstack = backstack,
      transactionPortalEntries = mutablePortalEntries.value.toMutableList(),
      transactionBackstackEntries = mutableBackstackEntries.value.toMutableList(),
      isForBackstack = false,
      defaultTransitions = defaultTransitions,
      doesIncompatibleStateThrow = doesIncompatibleStateThrow
    )
  }

  fun <R> transact(
    builder: PortalEntryBuilder<PortalKey>.() -> R
  ) = requireNotNull(transactionBuilder) {
    "Cannot transact if not in a transaction"
  }.builder()

  fun rollbackTransaction() {
    transactionBuilder = null
  }

  fun commitTransaction() {
    transactionBuilder?.build()?.let { (newPortals, newBackstackStack) ->
      mutablePortalEntries.value = newPortals
      mutableBackstackEntries.value = newBackstackStack
    }
    transactionBuilder = null
  }
}
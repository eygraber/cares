package com.eygraber.cares.nav

import com.eygraber.cares.StateSerializer
import com.eygraber.cares.nav.internal.BackstackEntrySaveState
import com.eygraber.cares.nav.internal.BackstackSaveState
import com.eygraber.cares.nav.internal.RenderNodeHolder
import com.eygraber.cares.nav.internal.WindowMutation
import com.eygraber.cares.nav.internal.WindowMutationBuilder
import com.eygraber.cares.nav.internal.applyMutations
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer

@DslMarker
internal annotation class BackstackDsl

public interface Backstack<FactoryKey> {
  public val size: Int

  public fun update(
    backstackEntryId: String,
    transition: ((FactoryKey, String) -> NavWindowTransition?)? = null,
    @BackstackDsl builder: NavWindow.UpdateBuilder<FactoryKey>.() -> Unit
  )

  public fun isEntryInBackstack(backstackEntryId: String): Boolean

  public fun isEntryAtTopOfBackstack(backstackEntryId: String): Boolean

  public fun clearBackstack(
    transition: ((FactoryKey, String) -> NavWindowTransition?)? = null
  ): Boolean

  public fun popBackstack(
    untilBackstackEntryId: String? = null,
    inclusive: Boolean = true,
    transition: ((FactoryKey, String) -> NavWindowTransition?)? = null
  ): Boolean
}

internal class BackstackImpl<FactoryKey>(
  private val nodes: MutableStateFlow<List<RenderNodeHolder<FactoryKey>>>,
  private val stateSerializer: StateSerializer,
  private val renderNodeFactory: RenderNodeFactory<FactoryKey>
) : Backstack<FactoryKey> {
  private val stack = atomic(emptyList<BackstackEntry<FactoryKey>>())

  internal fun restore(saveState: BackstackSaveState<FactoryKey>) {
    stack.value = saveState.toBackstack()
  }

  override val size: Int get() = stack.value.size

  override fun update(
    backstackEntryId: String,
    transition: ((FactoryKey, String) -> NavWindowTransition?)?,
    builder: NavWindow.UpdateBuilder<FactoryKey>.() -> Unit
  ) {
    val (mutations, backstackMutations) = WindowMutationBackstackBuilder(
      WindowMutationBuilder<FactoryKey>(stateSerializer)
    ).apply(builder).build()

    stack.value = stack.value + listOf(
      BackstackEntry(
        id = backstackEntryId,
        mutations = backstackMutations
      )
    )

    nodes.value = nodes.value.applyMutations(
      mutations,
      stateSerializer,
      renderNodeFactory,
      transition
    )
  }

  override fun isEntryInBackstack(backstackEntryId: String) =
    stack.value.indexOfLast { it.id == backstackEntryId } >= 0

  override fun isEntryAtTopOfBackstack(backstackEntryId: String) =
    stack.value.lastOrNull()?.id == backstackEntryId

  override fun clearBackstack(transition: ((FactoryKey, String) -> NavWindowTransition?)?): Boolean {
    val originalSize = size

    while(size > 0) {
      tryToActuallyPopBackstack(transition) {
        true
      }
    }

    return originalSize > size
  }

  override fun popBackstack(
    untilBackstackEntryId: String?,
    inclusive: Boolean,
    transition: ((FactoryKey, String) -> NavWindowTransition?)?
  ): Boolean {
    val originalSize = size

    var stop = false
    do {
      tryToActuallyPopBackstack(transition) { entryToPop ->
        if(untilBackstackEntryId == null || entryToPop.id == untilBackstackEntryId) {
          stop = true

          untilBackstackEntryId == null || inclusive
        }
        else {
          true
        }
      }
    } while(!stop && size > 0)

    return originalSize > size
  }

  private fun tryToActuallyPopBackstack(
    transition: ((FactoryKey, String) -> NavWindowTransition?)? = null,
    popPredicate: (BackstackEntry<FactoryKey>) -> Boolean
  ) = when(val peek = stack.value.lastOrNull()) {
    null -> false

    else -> when {
      popPredicate(peek) -> {
        stack.value = stack.value.dropLast(1)

        nodes.value = nodes.value.applyMutations(
          peek.mutations,
          stateSerializer,
          renderNodeFactory,
          transition
        )

        true
      }

      else -> false
    }
  }

  fun toSaveState() = BackstackSaveState(
    stack = stack.value.map(BackstackEntry<FactoryKey>::toSaveState)
  )
}

public fun <FactoryKey> Backstack<FactoryKey>.updateWithBackstack(
  backstackEntryId: FactoryKey,
  transition: ((FactoryKey, String) -> NavWindowTransition?)? = null,
  builder: NavWindow.UpdateBuilder<FactoryKey>.() -> Unit
) {
  update(backstackEntryId.toString(), transition, builder)
}

public fun <FactoryKey> Backstack<FactoryKey>.isEntryInBackstack(
  backstackEntryId: FactoryKey
): Boolean = isEntryInBackstack(backstackEntryId.toString())

public fun <FactoryKey> Backstack<FactoryKey>.isEntryAtTopOfBackstack(
  backstackEntryId: FactoryKey
): Boolean = isEntryAtTopOfBackstack(backstackEntryId.toString())

public fun <FactoryKey> Backstack<FactoryKey>.popBackstack(
  untilBackstackEntryId: FactoryKey? = null,
  inclusive: Boolean = true,
  transition: ((FactoryKey, String) -> NavWindowTransition?)? = null
): Boolean = popBackstack(untilBackstackEntryId.toString(), inclusive, transition)

internal data class BackstackEntry<FactoryKey>(
  val id: String,
  val mutations: List<WindowMutation<FactoryKey>>
) {
  fun toSaveState() = BackstackEntrySaveState(
    id = id,
    mutations = mutations.map(WindowMutation<FactoryKey>::toSaveState)
  )
}

internal class WindowMutationBackstackBuilder<FactoryKey>(
  private val builder: WindowMutationBuilder<FactoryKey>
) : NavWindow.UpdateBuilder<FactoryKey> {
  private val backstackMutations = mutableListOf<WindowMutation<FactoryKey>>()

  override fun add(key: FactoryKey, isAttached: Boolean, isHidden: Boolean, id: String) {
    builder.add(key, isAttached, isHidden, id)

    backstackMutations += WindowMutation.Remove(
      key = key,
      id = id
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
    builder.add(key, args, argsSerializer, isAttached, isHidden, id)

    backstackMutations += WindowMutation.Remove(
      key = key,
      id = id
    )
  }

  override fun remove(key: FactoryKey, id: String) {
    builder.remove(key, id)
  }

  override fun attach(key: FactoryKey, id: String, isHidden: Boolean) {
    builder.attach(key, id, isHidden)

    backstackMutations += WindowMutation.Detach(
      key = key,
      id = id,
      isBeingSentToBackstack = true
    )
  }

  override fun detach(key: FactoryKey, id: String) {
    builder.detach(key, id, isBeingSentToBackstack = true)

    backstackMutations += WindowMutation.Attach(
      key = key,
      id = id,
      isHidden = null,
      isBeingRestoredFromBackstack = true
    )
  }

  override fun show(key: FactoryKey, id: String) {
    builder.show(key, id)

    backstackMutations += WindowMutation.Hide(
      key = key,
      id = id
    )
  }

  override fun hide(key: FactoryKey, id: String) {
    builder.hide(key, id)

    backstackMutations += WindowMutation.Show(
      key = key,
      id = id
    )
  }

  fun build() = builder.build() to backstackMutations
}

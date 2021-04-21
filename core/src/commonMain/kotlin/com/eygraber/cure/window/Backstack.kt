package com.eygraber.cure.window

import androidx.compose.animation.ExperimentalAnimationApi
import com.eygraber.cure.RenderNode
import com.eygraber.cure.StateSerializer
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer

@DslMarker
internal annotation class BackstackDsl

@ExperimentalAnimationApi
public interface Backstack<FactoryKey> {
  public val size: Int

  public fun update(
      backstackEntryId: String,
      transitionOverride: ((FactoryKey, String) -> TransitionOverride?)? = null,
      @BackstackDsl builder: RenderWindow.UpdateBuilder<FactoryKey>.() -> Unit
  )

  public fun isEntryInBackstack(backstackEntryId: String): Boolean

  public fun isEntryAtTopOfBackstack(backstackEntryId: String): Boolean

  public fun clearBackstack(
      transitionOverride: ((FactoryKey, String) -> TransitionOverride?)? = null
  ): Boolean

  public fun popBackstack(
      untilBackstackEntryId: String? = null,
      inclusive: Boolean = true,
      transitionOverride: ((FactoryKey, String) -> TransitionOverride?)? = null
  ): Boolean
}

@ExperimentalAnimationApi
internal class BackstackImpl<FactoryKey>(
    private val nodes: MutableStateFlow<List<RenderNodeHolder<FactoryKey>>>,
    private val stateSerializer: StateSerializer,
    private val renderNodeFactoryFactory: (FactoryKey) -> RenderNode.Factory<*, *>,
    saveState: BackstackSaveState<FactoryKey>? = null
) : Backstack<FactoryKey> {
  private val stack = atomic(saveState?.toBackstack() ?: emptyList())

  override val size: Int get() = stack.value.size

  override fun update(
      backstackEntryId: String,
      transitionOverride: ((FactoryKey, String) -> TransitionOverride?)?,
      builder: RenderWindow.UpdateBuilder<FactoryKey>.() -> Unit
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
        renderNodeFactoryFactory,
        transitionOverride
    )
  }

  override fun isEntryInBackstack(backstackEntryId: String) =
      stack.value.indexOfLast { it.id == backstackEntryId } >= 0

  override fun isEntryAtTopOfBackstack(backstackEntryId: String) =
      stack.value.lastOrNull()?.id == backstackEntryId

  override fun clearBackstack(transitionOverride: ((FactoryKey, String) -> TransitionOverride?)?): Boolean {
    val originalSize = size

    while(size > 0) {
      tryToActuallyPopBackstack(transitionOverride) {
        true
      }
    }

    return originalSize > size
  }

  override fun popBackstack(
      untilBackstackEntryId: String?,
      inclusive: Boolean,
      transitionOverride: ((FactoryKey, String) -> TransitionOverride?)?
  ): Boolean {
    val originalSize = size

    var stop = false
    do {
      tryToActuallyPopBackstack(transitionOverride) { entryToPop ->
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
      transitionOverride: ((FactoryKey, String) -> TransitionOverride?)? = null,
      popPredicate: (BackstackEntry<FactoryKey>) -> Boolean
  ) = when(val peek = stack.value.lastOrNull()) {
    null -> false

    else -> when {
      popPredicate(peek) -> {
        stack.value = stack.value.dropLast(1)

        nodes.value = nodes.value.applyMutations(
            peek.mutations,
            stateSerializer,
            renderNodeFactoryFactory,
            transitionOverride
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

@ExperimentalAnimationApi public fun <FactoryKey> Backstack<FactoryKey>.updateWithBackstack(
    backstackEntryId: FactoryKey,
    transitionOverride: ((FactoryKey, String) -> TransitionOverride?)? = null,
    builder: RenderWindow.UpdateBuilder<FactoryKey>.() -> Unit
) {
  update(backstackEntryId.toString(), transitionOverride, builder)
}

@ExperimentalAnimationApi public fun <FactoryKey> Backstack<FactoryKey>.isEntryInBackstack(
    backstackEntryId: FactoryKey
): Boolean = isEntryInBackstack(backstackEntryId.toString())

@ExperimentalAnimationApi public fun <FactoryKey> Backstack<FactoryKey>.isEntryAtTopOfBackstack(
    backstackEntryId: FactoryKey
): Boolean = isEntryAtTopOfBackstack(backstackEntryId.toString())

@ExperimentalAnimationApi public fun <FactoryKey> Backstack<FactoryKey>.popBackstack(
    untilBackstackEntryId: FactoryKey? = null,
    inclusive: Boolean = true,
    transitionOverride: ((FactoryKey, String) -> TransitionOverride?)? = null
): Boolean = popBackstack(untilBackstackEntryId.toString(), inclusive, transitionOverride)

internal data class BackstackEntry<FactoryKey>(
    val id: String,
    val mutations: List<WindowMutation<FactoryKey>>
) {
  fun toSaveState() = BackstackEntrySaveState(
      id = id,
      mutations = mutations.map(WindowMutation<FactoryKey>::toSaveState)
  )
}

@ExperimentalAnimationApi
internal class WindowMutationBackstackBuilder<FactoryKey>(
    private val builder: WindowMutationBuilder<FactoryKey>
) : RenderWindow.UpdateBuilder<FactoryKey> {
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

package com.eygraber.cure.window

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.eygraber.cure.RenderNode
import com.eygraber.cure.StateSerializer
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

public data class RenderNodeArgs<FactoryKey>(
  val key: FactoryKey,
  val args: RenderWindow.SavedArgs?,
  val savedState: RenderWindow.SavedState?
)

public typealias RenderNodeFactory<FactoryKey> = (RenderNodeArgs<FactoryKey>) -> RenderNode<*, *>

public class RenderWindow<FactoryKey>(
  private val factoryKeySerializer: KSerializer<FactoryKey>,
  private val stateSerializer: StateSerializer,
  restoreState: ByteArray? = null,
  private val renderNodeFactory: RenderNodeFactory<FactoryKey>
) {
  @Composable
  public fun render() {
    val nodes by nodes.collectAsState()

    for(node in nodes) {
      RenderWindowNode(node)
    }
  }

  public fun update(
    transition: ((FactoryKey, String) -> RenderWindowTransition?)? = null,
    @UpdateBuilderDsl builder: UpdateBuilder<FactoryKey>.() -> Unit
  ) {
    lock.withLock {
      nodes.value = nodes.value.applyMutations(
        WindowMutationBuilder<FactoryKey>(stateSerializer).apply(builder).build(),
        stateSerializer,
        renderNodeFactory,
        transition
      )
    }
  }

  public fun <R> withBackstack(
    @BackstackDsl block: Backstack<FactoryKey>.() -> R
  ) {
    lock.withLock {
      backstack.block()
    }
  }

  public fun serialize(): ByteArray = lock.withLock {
    val data = RenderWindowSaveState(
      nodes = nodes.value.mapNotNull { nodeHolder ->
        nodeHolder.toSaveState(stateSerializer)
      },
      backstack = backstack.toSaveState()
    )

    return stateSerializer.serialize(data, RenderWindowSaveState.serializer(factoryKeySerializer))
  }

  public fun contains(key: FactoryKey, id: String = key.toString()): Boolean =
    nodes.value.findLast { holder ->
      holder.key == key && holder.id == id
    } != null

  public fun updates(): Flow<Unit> = nodes.map {}

  @Composable
  private fun RenderWindowNode(
    holder: RenderNodeHolder<FactoryKey>
  ) {
    val isBackstackOp = when(holder) {
      is RenderNodeHolder.Attached<*> -> holder.isBeingRestoredFromBackstack
      is RenderNodeHolder.Disappearing<*> -> holder.isBeingSentToBackstack
      else -> false
    }

    val node = when(holder) {
      is RenderNodeHolder.Attached<*> -> holder.node
      is RenderNodeHolder.Disappearing<*> -> holder.node
      else -> null
    }

    val (enterTransition, exitTransition) = when(val override = holder.transition) {
      null -> RenderWindowTransitions.Default.getEnterAndExitTransitions(
        isShowOrHide = holder.isShowOrHideMutation,
        isBackstackOp = isBackstackOp
      )

      RenderWindowTransition.NoTransition -> null

      is RenderWindowTransition.Transitions -> override.enter to override.exit
    } ?: run {
      if(holder is RenderNodeHolder.Disappearing<FactoryKey>) {
        applyDisappearingMutation(holder)
      }
      else {
        node?.render()
      }

      return
    }

    val isContentVisible = !holder.isHidden && holder is RenderNodeHolder.Attached<*>
    val isContentInitiallyVisible = holder.wasContentPreviouslyVisible

    val visibleState = remember {
      MutableTransitionState(isContentInitiallyVisible)
    }.apply { targetState = isContentVisible }

    @OptIn(ExperimentalAnimationApi::class)
    AnimatedVisibility(
      visibleState = visibleState,
      enter = enterTransition,
      exit = exitTransition
    ) {
      node?.render()

      if(holder is RenderNodeHolder.Disappearing<FactoryKey>) {
        DisposableEffect(Unit) {
          onDispose {
            applyDisappearingMutation(holder)
          }
        }
      }
    }
  }

  private fun applyDisappearingMutation(holder: RenderNodeHolder.Disappearing<FactoryKey>) = lock.withLock {
    nodes.value = nodes.value.applyMutations(
      listOf(
        WindowMutation.Disappearing(
          key = holder.key,
          id = holder.id,
          isRemoving = holder.isRemoving,
          isBeingSentToBackstack = holder.isBeingSentToBackstack
        )
      ),
      stateSerializer,
      renderNodeFactory,
      holder.transition?.let { { _: FactoryKey, _: String -> it } }
    )
  }

  private val lock = reentrantLock()
  private val nodes = MutableStateFlow<List<RenderNodeHolder<FactoryKey>>>(emptyList())
  private val backstack: BackstackImpl<FactoryKey>

  init {
    if(restoreState == null) {
      backstack = BackstackImpl(
        nodes,
        stateSerializer,
        renderNodeFactory
      )
    }
    else {
      val state = stateSerializer.deserialize(
        restoreState,
        RenderWindowSaveState.serializer(factoryKeySerializer)
      )

      backstack = BackstackImpl(
        nodes,
        stateSerializer,
        renderNodeFactory,
        state.backstack
      )
      nodes.value = state.toRenderNodeHolders(stateSerializer, renderNodeFactory)
    }
  }

  public interface UpdateBuilder<FactoryKey> {
    public fun add(
      key: FactoryKey,
      isAttached: Boolean = true,
      isHidden: Boolean = false,
      id: String = key.toString()
    )

    public fun <T> add(
      key: FactoryKey,
      args: T,
      argsSerializer: KSerializer<T>,
      isAttached: Boolean = true,
      isHidden: Boolean = false,
      id: String = key.toString()
    )

    public fun remove(
      key: FactoryKey,
      id: String = key.toString()
    )

    public fun attach(
      key: FactoryKey,
      id: String = key.toString(),
      isHidden: Boolean = false
    )

    public fun detach(
      key: FactoryKey,
      id: String = key.toString()
    )

    public fun show(
      key: FactoryKey,
      id: String = key.toString()
    )

    public fun hide(
      key: FactoryKey,
      id: String = key.toString()
    )
  }

  public class SavedArgs(
    @PublishedApi internal val data: ByteArray,
    @PublishedApi internal val serializer: StateSerializer
  ) {
    public inline fun <reified Arg> args(): Arg =
      serializer.deserialize(data, serializer())
  }

  public class SavedState(
    @PublishedApi internal val data: ByteArray,
    @PublishedApi internal val serializer: StateSerializer
  ) {
    public inline fun <reified Arg> state(): Arg =
      serializer.deserialize(data, serializer())
  }
}

public inline fun <reified T, FactoryKey> RenderWindow.UpdateBuilder<FactoryKey>.add(
  key: FactoryKey,
  args: T,
  isAttached: Boolean = true,
  isHidden: Boolean = false,
  id: String = key.toString()
) {
  add(
    key = key,
    args = args,
    argsSerializer = serializer(),
    isAttached = isAttached,
    isHidden = isHidden,
    id = id
  )
}

@DslMarker
private annotation class UpdateBuilderDsl

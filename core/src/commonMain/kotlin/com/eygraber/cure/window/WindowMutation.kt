package com.eygraber.cure.window

import androidx.compose.animation.ExperimentalAnimationApi
import com.eygraber.cure.RenderNode
import com.eygraber.cure.StateSerializer

internal sealed class WindowMutation<FactoryKey> {
  abstract val key: FactoryKey
  abstract val id: String

  abstract fun toSaveState(): WindowMutationSaveState<FactoryKey>

  @Suppress("ArrayInDataClass")
  data class Add<FactoryKey>(
    override val key: FactoryKey,
    override val id: String,
    val args: ByteArray?,
    val isAttached: Boolean,
    val isHidden: Boolean
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = WindowMutationSaveStateType.Add,
      key = key,
      id = id,
      args = args,
      isAttached = isAttached,
      isHidden = isHidden,
      isBeingRestoredFromOrSentToBackstack = false
    )
  }

  data class Remove<FactoryKey>(
    override val key: FactoryKey,
    override val id: String
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = WindowMutationSaveStateType.Remove,
      key = key,
      id = id,
      isBeingRestoredFromOrSentToBackstack = false
    )
  }

  data class Attach<FactoryKey>(
    override val key: FactoryKey,
    override val id: String,
    val isHidden: Boolean?,
    val isBeingRestoredFromBackstack: Boolean
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = WindowMutationSaveStateType.Attach,
      key = key,
      id = id,
      isBeingRestoredFromOrSentToBackstack = isBeingRestoredFromBackstack
    )
  }

  data class Detach<FactoryKey>(
    override val key: FactoryKey,
    override val id: String,
    val isBeingSentToBackstack: Boolean
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = WindowMutationSaveStateType.Detach,
      key = key,
      id = id,
      isBeingRestoredFromOrSentToBackstack = isBeingSentToBackstack
    )
  }

  data class Show<FactoryKey>(
    override val key: FactoryKey,
    override val id: String
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = WindowMutationSaveStateType.Show,
      key = key,
      id = id,
      isBeingRestoredFromOrSentToBackstack = false
    )
  }

  data class Hide<FactoryKey>(
    override val key: FactoryKey,
    override val id: String
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = WindowMutationSaveStateType.Hide,
      key = key,
      id = id,
      isBeingRestoredFromOrSentToBackstack = false
    )
  }

  data class Disappearing<FactoryKey>(
    override val key: FactoryKey,
    override val id: String,
    val isRemoving: Boolean,
    val isBeingSentToBackstack: Boolean
  ) : WindowMutation<FactoryKey>() {
    override fun toSaveState(): WindowMutationSaveState<FactoryKey> = WindowMutationSaveState(
      type = when {
        isRemoving -> WindowMutationSaveStateType.Remove
        else -> WindowMutationSaveStateType.Detach
      },
      key = key,
      id = id,
      isBeingRestoredFromOrSentToBackstack = isBeingSentToBackstack
    )
  }
}

@ExperimentalAnimationApi
internal fun <FactoryKey> List<RenderNodeHolder<FactoryKey>>.applyMutations(
  mutations: List<WindowMutation<FactoryKey>>,
  stateSerializer: StateSerializer,
  renderNodeFactoryFactory: (FactoryKey) -> RenderNode.Factory<*, *>,
  transitionOverrider: ((FactoryKey, String) -> TransitionOverride?)? = null
): List<RenderNodeHolder<FactoryKey>> {
  val window = toMutableList()

  fun WindowMutation<FactoryKey>.applyMutation(
    mutate: (RenderNodeHolder<FactoryKey>, TransitionOverride?) -> RenderNodeHolder<FactoryKey>?
  ) {
    window
      .indexOfLast { holder -> holder.key == key && holder.id == id }
      .takeIf { it > -1 }
      ?.let { index ->
        val holder = window[index]
        val newHolder = mutate(holder, transitionOverrider?.invoke(holder.key, holder.id))
        if(newHolder == null) {
          window.removeAt(index)
        }
        else {
          window[index] = newHolder
        }
      }
  }

  for(mutation in mutations) {
    when(mutation) {
      is WindowMutation.Add<FactoryKey> -> window.add(
        RenderNodeHolder.Attached(
          key = mutation.key,
          id = mutation.id,
          isShowOrHideMutation = false,
          wasContentPreviouslyVisible = false,
          isHidden = mutation.isHidden,
          args = mutation.args,
          node = renderNodeFactoryFactory(mutation.key).create(
            args = mutation.args,
            savedState = null,
            serializer = stateSerializer
          ),
          isBeingRestoredFromBackstack = false,
          transitionOverride = transitionOverrider?.invoke(mutation.key, mutation.id)
        )
      )

      is WindowMutation.Remove -> mutation.applyMutation { holder, transitionOverride ->
        if(holder is RenderNodeHolder.Attached<*>) {
          RenderNodeHolder.Disappearing(
            key = mutation.key,
            id = mutation.id,
            wasContentPreviouslyVisible = !holder.isHidden,
            isHidden = holder.isHidden,
            args = holder.args,
            node = holder.node,
            isRemoving = true,
            isBeingSentToBackstack = false,
            transitionOverride = transitionOverride
          )
        }
        else {
          null
        }
      }

      is WindowMutation.Attach -> mutation.applyMutation { holder, transitionOverride ->
        if(holder is RenderNodeHolder.Detached<*>) {
          RenderNodeHolder.Attached(
            key = mutation.key,
            id = mutation.id,
            isShowOrHideMutation = false,
            wasContentPreviouslyVisible = false,
            isHidden = mutation.isHidden ?: holder.isHidden,
            args = holder.args,
            node = renderNodeFactoryFactory(mutation.key).create(
              args = holder.args,
              savedState = holder.savedState,
              serializer = stateSerializer
            ),
            isBeingRestoredFromBackstack = mutation.isBeingRestoredFromBackstack,
            transitionOverride = transitionOverride
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Detach -> mutation.applyMutation { holder, transitionOverride ->
        if(holder is RenderNodeHolder.Attached<*>) {
          RenderNodeHolder.Disappearing(
            key = mutation.key,
            id = mutation.id,
            wasContentPreviouslyVisible = !holder.isHidden,
            isHidden = holder.isHidden,
            args = holder.args,
            node = holder.node,
            isRemoving = false,
            isBeingSentToBackstack = mutation.isBeingSentToBackstack,
            transitionOverride = transitionOverride
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Show -> mutation.applyMutation { holder, transitionOverride ->
        if(holder is RenderNodeHolder.Attached<*> && holder.isHidden) {
          RenderNodeHolder.Attached(
            key = mutation.key,
            id = mutation.id,
            isShowOrHideMutation = true,
            wasContentPreviouslyVisible = false,
            isHidden = false,
            args = holder.args,
            node = holder.node,
            isBeingRestoredFromBackstack = false,
            transitionOverride = transitionOverride
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Hide -> mutation.applyMutation { holder, transitionOverride ->
        if(holder is RenderNodeHolder.Attached<*> && !holder.isHidden) {
          RenderNodeHolder.Attached(
            key = mutation.key,
            id = mutation.id,
            isShowOrHideMutation = true,
            wasContentPreviouslyVisible = true,
            isHidden = true,
            args = holder.args,
            node = holder.node,
            isBeingRestoredFromBackstack = false,
            transitionOverride = transitionOverride
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Disappearing -> mutation.applyMutation { holder, transitionOverride ->
        if(holder is RenderNodeHolder.Disappearing<*>) {
          when {
            holder.isRemoving -> null

            else -> RenderNodeHolder.Detached(
              key = mutation.key,
              id = mutation.id,
              wasContentPreviouslyVisible = !holder.isHidden,
              isHidden = holder.isHidden,
              args = holder.args,
              savedState = holder.node.serialize(stateSerializer),
              transitionOverride = transitionOverride
            )
          }
        }
        else {
          holder
        }
      }
    }
  }

  return window.toList()
}

package com.eygraber.cares.nav.internal

import com.eygraber.cares.SerializableRenderNode
import com.eygraber.cares.StateSerializer
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.nav.NavWindowTransition
import com.eygraber.cares.nav.RenderNodeArgs
import com.eygraber.cares.nav.RenderNodeFactory

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

internal fun <FactoryKey> List<RenderNodeHolder<FactoryKey>>.applyMutations(
  navWindow: NavWindow<FactoryKey>,
  mutations: List<WindowMutation<FactoryKey>>,
  stateSerializer: StateSerializer,
  renderNodeFactory: RenderNodeFactory<FactoryKey>,
  transitionr: ((FactoryKey, String) -> NavWindowTransition?)? = null
): List<RenderNodeHolder<FactoryKey>> {
  val window = toMutableList()

  fun WindowMutation<FactoryKey>.applyMutation(
    mutate: (RenderNodeHolder<FactoryKey>, NavWindowTransition?) -> RenderNodeHolder<FactoryKey>?
  ) {
    window
      .indexOfLast { holder -> holder.key == key && holder.id == id }
      .takeIf { it > -1 }
      ?.let { index ->
        val holder = window[index]
        val newHolder = mutate(holder, transitionr?.invoke(holder.key, holder.id))
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
          node = navWindow.renderNodeFactory(
            RenderNodeArgs(
              key = mutation.key,
              args = mutation.args?.let { args ->
                NavWindow.SavedArgs(args, stateSerializer)
              },
              savedState = null
            )
          ),
          isBeingRestoredFromBackstack = false,
          transition = transitionr?.invoke(mutation.key, mutation.id)
        )
      )

      is WindowMutation.Remove -> mutation.applyMutation { holder, transition ->
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
            transition = transition
          )
        }
        else {
          null
        }
      }

      is WindowMutation.Attach -> mutation.applyMutation { holder, transition ->
        if(holder is RenderNodeHolder.Detached<*>) {
          RenderNodeHolder.Attached(
            key = mutation.key,
            id = mutation.id,
            isShowOrHideMutation = false,
            wasContentPreviouslyVisible = false,
            isHidden = mutation.isHidden ?: holder.isHidden,
            args = holder.args,
            node = navWindow.renderNodeFactory(
              RenderNodeArgs(
                key = mutation.key,
                args = holder.args?.let { args ->
                  NavWindow.SavedArgs(args, stateSerializer)
                },
                savedState = holder.savedState?.let { savedState ->
                  NavWindow.SavedState(savedState, stateSerializer)
                }
              )
            ),
            isBeingRestoredFromBackstack = mutation.isBeingRestoredFromBackstack,
            transition = transition
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Detach -> mutation.applyMutation { holder, transition ->
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
            transition = transition
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Show -> mutation.applyMutation { holder, transition ->
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
            transition = transition
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Hide -> mutation.applyMutation { holder, transition ->
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
            transition = transition
          )
        }
        else {
          holder
        }
      }

      is WindowMutation.Disappearing -> mutation.applyMutation { holder, transition ->
        if(holder is RenderNodeHolder.Disappearing<*>) {
          when {
            holder.isRemoving -> null

            else -> RenderNodeHolder.Detached(
              key = mutation.key,
              id = mutation.id,
              wasContentPreviouslyVisible = !holder.isHidden,
              isHidden = holder.isHidden,
              args = holder.args,
              savedState = (holder.node as? SerializableRenderNode<*>)?.serializeLatestState(stateSerializer),
              transition = transition
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

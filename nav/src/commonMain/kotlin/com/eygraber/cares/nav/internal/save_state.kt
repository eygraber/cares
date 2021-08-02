package com.eygraber.cares.nav.internal

import com.eygraber.cares.SerializableRenderNode
import com.eygraber.cares.StateSerializer
import com.eygraber.cares.nav.BackstackEntry
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.nav.NavWindowParent
import com.eygraber.cares.nav.RenderNodeArgs
import com.eygraber.cares.nav.RenderNodeFactory
import kotlinx.serialization.Serializable

@Serializable
internal class NavWindowSaveState<FactoryKey>(
  val nodes: List<RenderNodeHolderSaveState<FactoryKey>>,
  val backstack: BackstackSaveState<FactoryKey>
) {
  fun toRenderNodeHolders(
    navWindow: NavWindow<FactoryKey>,
    stateSerializer: StateSerializer,
    renderNodeFactory: RenderNodeFactory<FactoryKey>
  ) = nodes.map { it.toRenderNodeHolder(navWindow, stateSerializer, renderNodeFactory) }
}

internal fun <FactoryKey> RenderNodeHolder<FactoryKey>.toSaveState(
  stateSerializer: StateSerializer
) = when(this) {
  is RenderNodeHolder.Attached -> RenderNodeHolderSaveState(
    key = key,
    id = id,
    wasContentPreviouslyVisible = wasContentPreviouslyVisible,
    isHidden = isHidden,
    args = args,
    savedState = (node as? SerializableRenderNode<*>)?.serializeLatestState(stateSerializer),
    childNavWindowSavedState = (node as? NavWindowParent<*>)?.childNavWindow?.serialize(),
    isAttached = true
  )

  is RenderNodeHolder.Disappearing -> when {
    isRemoving -> null

    else -> RenderNodeHolderSaveState(
      key = key,
      id = id,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isHidden = isHidden,
      args = args,
      savedState = (node as? SerializableRenderNode<*>)?.serializeLatestState(stateSerializer),
      childNavWindowSavedState = (node as? NavWindowParent<*>)?.childNavWindow?.serialize(),
      isAttached = true
    )
  }

  is RenderNodeHolder.Detached -> RenderNodeHolderSaveState(
    key = key,
    id = id,
    wasContentPreviouslyVisible = wasContentPreviouslyVisible,
    isHidden = isHidden,
    args = args,
    savedState = savedState,
    childNavWindowSavedState = childNavWindowSavedState,
    isAttached = false
  )
}

@Serializable
internal class RenderNodeHolderSaveState<FactoryKey>(
  val key: FactoryKey,
  val id: String,
  val wasContentPreviouslyVisible: Boolean,
  val isHidden: Boolean,
  val args: ByteArray?,
  val savedState: ByteArray?,
  val childNavWindowSavedState: ByteArray?,
  val isAttached: Boolean
) {
  fun toRenderNodeHolder(
    navWindow: NavWindow<FactoryKey>,
    stateSerializer: StateSerializer,
    renderNodeFactory: RenderNodeFactory<FactoryKey>
  ) = if(isAttached) {
    RenderNodeHolder.Attached(
      key = key,
      id = id,
      isShowOrHideMutation = false,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isHidden = isHidden,
      args = args,
      node = navWindow.renderNodeFactory(
        RenderNodeArgs(
          key = key,
          args = args?.let { args ->
            NavWindow.SavedArgs(args, stateSerializer)
          },
          savedState = savedState?.let { savedState ->
            NavWindow.SavedState(savedState, stateSerializer)
          }
        )
      ).apply {
        if(this is NavWindowParent<*> && childNavWindowSavedState != null) {
          childNavWindow.restore(childNavWindowSavedState)
        }
      },
      isBeingRestoredFromBackstack = false
    )
  }
  else {
    RenderNodeHolder.Detached(
      key = key,
      id = id,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isHidden = isHidden,
      args = args,
      savedState = savedState,
      childNavWindowSavedState = childNavWindowSavedState
    )
  }
}

@Serializable
internal class BackstackSaveState<FactoryKey>(
  val stack: List<BackstackEntrySaveState<FactoryKey>>
) {
  fun toBackstack() = stack.map(BackstackEntrySaveState<FactoryKey>::toBackstackEntry)
}

@Serializable
internal class BackstackEntrySaveState<FactoryKey>(
  val id: String,
  val mutations: List<WindowMutationSaveState<FactoryKey>>
) {
  fun toBackstackEntry() = BackstackEntry(
    id = id,
    mutations = mutations.map { mutation ->
      when(mutation.type) {
        WindowMutationSaveStateType.Add -> WindowMutation.Add(
          key = mutation.key,
          id = mutation.id,
          args = mutation.args,
          isAttached = mutation.isAttached ?: true,
          isHidden = mutation.isHidden ?: false
        )

        WindowMutationSaveStateType.Remove -> WindowMutation.Remove(
          key = mutation.key,
          id = mutation.id
        )

        WindowMutationSaveStateType.Attach -> WindowMutation.Attach(
          key = mutation.key,
          id = mutation.id,
          isHidden = mutation.isHidden ?: false,
          isBeingRestoredFromBackstack = mutation.isBeingRestoredFromOrSentToBackstack
        )

        WindowMutationSaveStateType.Detach -> WindowMutation.Detach(
          key = mutation.key,
          id = mutation.id,
          isBeingSentToBackstack = mutation.isBeingRestoredFromOrSentToBackstack
        )

        WindowMutationSaveStateType.Show -> WindowMutation.Show(
          key = mutation.key,
          id = mutation.id
        )

        WindowMutationSaveStateType.Hide -> WindowMutation.Hide(
          key = mutation.key,
          id = mutation.id
        )
      }
    }
  )
}

@Serializable
internal enum class WindowMutationSaveStateType {
  Add,
  Remove,
  Attach,
  Detach,
  Show,
  Hide
}

@Serializable
internal class WindowMutationSaveState<FactoryKey>(
  val type: WindowMutationSaveStateType,
  val key: FactoryKey,
  val id: String,
  val args: ByteArray? = null,
  val isAttached: Boolean? = null,
  val isHidden: Boolean? = null,
  val isBeingRestoredFromOrSentToBackstack: Boolean
)

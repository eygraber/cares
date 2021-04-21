package com.eygraber.cure.window

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Immutable
import com.eygraber.cure.RenderNode

@Suppress("ArrayInDataClass")
@ExperimentalAnimationApi
@Immutable
internal sealed class RenderNodeHolder<FactoryKey> {
  abstract val key: FactoryKey
  abstract val id: String

  // we need to know this so we can apply show/hide transitions
  abstract val isShowOrHideMutation: Boolean

  abstract val wasContentPreviouslyVisible: Boolean
  abstract val isHidden: Boolean

  abstract val args: ByteArray?

  abstract val transitionOverride: TransitionOverride?

  @Immutable
  data class Attached<FactoryKey>(
      override val key: FactoryKey,
      override val id: String,
      override val isShowOrHideMutation: Boolean,
      override val wasContentPreviouslyVisible: Boolean,
      override val isHidden: Boolean,
      override val args: ByteArray?,
      val node: RenderNode<*, *>,
      val isBeingRestoredFromBackstack: Boolean,
      override val transitionOverride: TransitionOverride? = null
  ) : RenderNodeHolder<FactoryKey>()

  @Immutable
  data class Disappearing<FactoryKey>(
      override val key: FactoryKey,
      override val id: String,
      override val wasContentPreviouslyVisible: Boolean,
      override val isHidden: Boolean,
      override val args: ByteArray?,
      val node: RenderNode<*, *>,
      val isRemoving: Boolean,
      val isBeingSentToBackstack: Boolean,
      override val transitionOverride: TransitionOverride? = null
  ) : RenderNodeHolder<FactoryKey>() {
    override val isShowOrHideMutation = false
  }

  @Immutable
  data class Detached<FactoryKey>(
      override val key: FactoryKey,
      override val id: String,
      override val wasContentPreviouslyVisible: Boolean,
      override val isHidden: Boolean,
      override val args: ByteArray?,
      val savedState: ByteArray?,
      override val transitionOverride: TransitionOverride? = null
  ) : RenderNodeHolder<FactoryKey>() {
    override val isShowOrHideMutation: Boolean = false
  }

  final override fun toString(): String {
    return "${this::class.simpleName}(key=$key, id='$id', isHidden=$isHidden)"
  }

  final override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other == null) return false
    if(this::class != other::class) return false

    other as RenderNodeHolder<*>

    if(key != other.key) return false
    if(id != other.id) return false
    if(isShowOrHideMutation != other.isShowOrHideMutation) return false
    if(wasContentPreviouslyVisible != other.wasContentPreviouslyVisible) return false
    if(isHidden != other.isHidden) return false

    return true
  }

  final override fun hashCode(): Int {
    var result = key?.hashCode() ?: 0
    result = 31 * result + id.hashCode()
    result = 31 * result + isShowOrHideMutation.hashCode()
    result = 31 * result + wasContentPreviouslyVisible.hashCode()
    result = 31 * result + isHidden.hashCode()
    return result
  }
}

package com.eygraber.cares.samples.nav.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import com.eygraber.cares.JsonStateSerializer
import com.eygraber.cares.nav.NavWindow
import com.eygraber.cares.samples.nav.android.number.NumberRenderNode
import com.eygraber.cares.samples.nav.android.number.NumberState
import com.eygraber.cares.samples.nav.android.parent.ParentNumberRenderNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class NumberKey {
  One,
  Two,
  Parent,
  Four,
  Five
}

class MainActivity : AppCompatActivity() {
  private val navWindow = NavWindow<NumberKey>(
    factoryKeySerializer = serializer(),
    stateSerializer = JsonStateSerializer()
  ) { args ->
    when(args.key) {
      NumberKey.One -> NumberRenderNode(this, NumberKey.Two, NumberState(1, true))
      NumberKey.Two -> NumberRenderNode(this, NumberKey.Parent, NumberState(2, true))
      NumberKey.Parent -> ParentNumberRenderNode(this, NumberKey.Four)
      NumberKey.Four -> NumberRenderNode(this, NumberKey.Five, NumberState(4, true))
      NumberKey.Five -> NumberRenderNode(this, NumberKey.One, NumberState(5, true))
    }
  }

  @OptIn(ExperimentalAnimationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    savedInstanceState
      ?.getByteArray("nav-window")
      ?.let(navWindow::restore)
      ?: run {
        navWindow.update {
          add(NumberKey.One)
        }
      }

    setContent {
      MaterialTheme(
        colors = darkColors(
          primary = Color(0xFFBB86FC),
          primaryVariant = Color(0xFF3700B3),
          secondary = Color(0xFF03DAC5)
        )
      ) {
        navWindow.render()
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putByteArray("nav-window", navWindow.serialize())
  }
}

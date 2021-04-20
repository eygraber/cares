package com.eygraber.cure.samples.simple_clock

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
fun main() {
  renderWindow.update {
    add(SimpleClockFactoryKey.Clock)
  }

  Window(title = "SimpleClock") {
    MaterialTheme(
      colors = darkColors()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            color = MaterialTheme.colors.background
          )
      ) {
        AppContent()
      }
    }
  }
}

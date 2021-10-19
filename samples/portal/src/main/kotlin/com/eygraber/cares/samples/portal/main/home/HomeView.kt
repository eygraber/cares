package com.eygraber.cares.samples.portal.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eygraber.cares.View

class HomeView : View<Unit> {
  override val vm = HomeViewModel()

  @Composable
  override fun render() {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize()
    ) {
      Button(
        onClick = vm::openAlarmsClicked
      ) {
        Icon(
          imageVector = Icons.Filled.Alarm,
          contentDescription = "Open alarm list"
        )
      }
    }
  }
}
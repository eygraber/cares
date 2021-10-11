package com.eygraber.cares.samples.nameandcount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.cares.View

class NameAndCountView : View<NameAndCountState> {
  override val vm = NameAndCountVM()

  private val state = vm.state

  @Composable
  override fun render() {
    Column(
      modifier = Modifier
        .fillMaxSize()
    ) {
      NameField()

      CountLabel()

      IncrementButton()
    }
  }

  @Composable
  private fun NameField() {
    TextField(
      value = state.name,
      onValueChange = { newName ->
        vm.onNameChanged(newName)
      },
      modifier = Modifier
        .fillMaxWidth()
    )
  }

  @Composable
  private fun ColumnScope.CountLabel() {
    Text(
      text = state.count.toString(),
      modifier = Modifier
        .padding(top = 100.dp)
        .align(Alignment.CenterHorizontally)
    )
  }

  @Composable
  private fun ColumnScope.IncrementButton() {
    Button(
      onClick = {
        vm.onIncrementClicked()
      },
      modifier = Modifier
        .padding(top = 16.dp)
        .align(Alignment.CenterHorizontally)
    ) {
      Text("Increment Count")
    }
  }
}

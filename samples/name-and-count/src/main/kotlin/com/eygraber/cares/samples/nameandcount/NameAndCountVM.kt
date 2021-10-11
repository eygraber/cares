package com.eygraber.cares.samples.nameandcount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.cares.VM

class NameAndCountVM : VM<NameAndCountState> {
  private val mutableState = object : NameAndCountState {
    override var name by mutableStateOf("")
    override var count by mutableStateOf(0)
  }

  override val state = mutableState

  fun onNameChanged(name: String) {
    mutableState.name = name
  }

  fun onIncrementClicked() {
    mutableState.count++
  }
}

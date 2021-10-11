package com.eygraber.cares.samples.nameandcount

import androidx.compose.runtime.State

interface NameAndCountState {
  val name: String
  val count: Int
}

interface NameAndCountState2 {
  val name: State<String>
  val count: State<Int>
}

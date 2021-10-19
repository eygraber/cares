package com.eygraber.cares.samples.portal.main

import androidx.compose.runtime.Immutable

@Immutable
interface MainState {
  val selectedTab: MainPortalKey
}

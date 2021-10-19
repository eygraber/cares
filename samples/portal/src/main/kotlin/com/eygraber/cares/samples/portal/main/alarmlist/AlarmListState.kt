package com.eygraber.cares.samples.portal.main.alarmlist

import androidx.compose.runtime.Stable

@Stable
interface AlarmListAlarm {
  val id: String
  val isEnabled: Boolean
  val time: String
}

@Stable
interface AlarmListState {
  val alarms: List<AlarmListAlarm>
}

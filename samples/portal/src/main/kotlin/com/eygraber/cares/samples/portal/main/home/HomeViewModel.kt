package com.eygraber.cares.samples.portal.main.home

import com.eygraber.cares.VM
import com.eygraber.cares.portal.push
import com.eygraber.cares.samples.portal.AppPortalKey
import com.eygraber.cares.samples.portal.appPortals
import com.eygraber.cares.samples.portal.main.alarmlist.AlarmListView

class HomeViewModel : VM<Unit> {
  override val state = Unit

  fun openAlarmsClicked() {
    appPortals.withTransaction {
      backstack.push(AppPortalKey.AlarmList) {
        add(AppPortalKey.AlarmList) {
          AlarmListView().render()
        }
      }
    }
  }
}

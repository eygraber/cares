package com.eygraber.cares.portal.internal

internal data class PortalBackstackEntry<PortalKey>(
  val id: String,
  val mutations: List<PortalBackstackMutation<PortalKey>>
)

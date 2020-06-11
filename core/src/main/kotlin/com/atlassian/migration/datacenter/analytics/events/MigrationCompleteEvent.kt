package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName

@EventName("atl.dc.migration.complete")
data class MigrationCompleteEvent (
        val pluginVersion: String,
        val runTime: Long  // Seconds
)
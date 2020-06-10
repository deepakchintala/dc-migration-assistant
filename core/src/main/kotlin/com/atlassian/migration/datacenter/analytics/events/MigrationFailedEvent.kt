package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName

@EventName("atl.dc.migration.failed")
data class MigrationFailedEvent(
        val pluginVersion: String,
        val totalTime: Long,
        val failedStage: String
)
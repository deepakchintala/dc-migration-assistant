package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName

@EventName("atl.dc.migration.started")
data class MigrationCreatedEvent(
        val pluginVersion: String
)
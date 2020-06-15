package com.atlassian.migration.datacenter.analytics.events

import com.atlassian.analytics.api.annotations.EventName

@EventName("atl.dc.migration.prerequisites")
data class MigrationPrerequisiteEvent(
        // NOTE: If additional properties are added here they should also be added to the file analytics_whitelist.json.
        val pluginVersion: String,
        val dbCompatible: Boolean,
        val dbType: String,
        val osCompatible: Boolean,
        val osType: String,
        val fsSizeCompatible: Boolean,
        val fsSize: Long
)
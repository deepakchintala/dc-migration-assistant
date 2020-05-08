package com.atlassian.migration.datacenter.spi

data class MigrationReadyStatus(val dbCompatible: Boolean,
                                val osCompatible: Boolean,
                                val fsSizeCompatible: Boolean);
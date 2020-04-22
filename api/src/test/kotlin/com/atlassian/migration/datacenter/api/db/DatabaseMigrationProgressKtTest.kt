package com.atlassian.migration.datacenter.api.db

import com.atlassian.migration.datacenter.spi.MigrationStage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DatabaseMigrationProgressKtTest {

    @Test
    fun testStageMapping() {
        assertEquals(DBMigrationStatus.NOT_STARTED, stageToStatus(MigrationStage.PROVISION_APPLICATION))
        assertEquals(DBMigrationStatus.EXPORTING, stageToStatus(MigrationStage.DB_MIGRATION_EXPORT))
        assertEquals(DBMigrationStatus.FAILED, stageToStatus(MigrationStage.ERROR))
    }
}
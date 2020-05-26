package com.atlassian.migration.datacenter.api.db

import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.MigrationStage.*
import java.time.Duration


enum class DBMigrationStatus {
    NOT_STARTED,
    FAILED,
    EXPORTING,
    UPLOADING,
    IMPORTING,
    DONE,
}


fun stageToStatus(stage: MigrationStage): DBMigrationStatus {
    // Could be data-driven, but this is clear enough.
    return when (stage) {
        NOT_STARTED,
        AUTHENTICATION,
        PROVISION_APPLICATION,
        PROVISION_APPLICATION_WAIT,
        PROVISION_MIGRATION_STACK,
        PROVISION_MIGRATION_STACK_WAIT,
        FS_MIGRATION_COPY,
        FS_MIGRATION_COPY_WAIT,
        OFFLINE_WARNING
        -> DBMigrationStatus.NOT_STARTED

        DB_MIGRATION_EXPORT,
        DB_MIGRATION_EXPORT_WAIT
        -> DBMigrationStatus.EXPORTING

        DB_MIGRATION_UPLOAD,
        DB_MIGRATION_UPLOAD_WAIT
        -> DBMigrationStatus.UPLOADING

        DATA_MIGRATION_IMPORT,
        DATA_MIGRATION_IMPORT_WAIT
        -> DBMigrationStatus.IMPORTING

        VALIDATE,
        CUTOVER,
        FINISHED
        -> DBMigrationStatus.DONE

        ERROR
        -> DBMigrationStatus.FAILED
    }
}

data class DatabaseMigrationStatus(val status: DBMigrationStatus, val elapsedTime: Duration)

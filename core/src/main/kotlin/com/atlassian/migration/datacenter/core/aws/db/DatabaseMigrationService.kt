/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atlassian.migration.datacenter.core.aws.db

import com.atlassian.migration.datacenter.core.aws.db.restore.DatabaseRestoreStageTransitionCallback
import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService
import com.atlassian.migration.datacenter.core.db.DatabaseMigrationJobRunner
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader.FileUploadException
import com.atlassian.migration.datacenter.core.util.MigrationRunner
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.DatabaseMigrationFailure
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport
import com.atlassian.scheduler.config.JobId
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class DatabaseMigrationService(private val tempDirectory: Path, private val migrationService: MigrationService,
                               private val migrationRunner: MigrationRunner, private val databaseArchivalService: DatabaseArchivalService,
                               private val stageTransitionCallback: DatabaseArchiveStageTransitionCallback,
                               private val s3UploadService: DatabaseArtifactS3UploadService?,
                               private val uploadStageTransitionCallback: DatabaseUploadStageTransitionCallback,
                               private val restoreService: SsmPsqlDatabaseRestoreService,
                               private val restoreStageTransitionCallback: DatabaseRestoreStageTransitionCallback,
                               private val migrationHelperDeploymentService: AWSMigrationHelperDeploymentService) {
    private val startTime = AtomicReference(Optional.empty<LocalDateTime>())

    /**
     * Start database dump and upload to S3 bucket. This is a blocking operation and
     * should be started from ExecutorService or preferably from ScheduledJob. The
     * status of the migration can be queried via getStatus().
     */
    @Throws(DatabaseMigrationFailure::class, InvalidMigrationStageError::class)
    fun performMigration(): FileSystemMigrationErrorReport {
        migrationService.transition(MigrationStage.DB_MIGRATION_EXPORT)
        startTime.set(Optional.of(LocalDateTime.now()))
        val pathToDatabaseFile = databaseArchivalService.archiveDatabase(tempDirectory, stageTransitionCallback)
        val report: FileSystemMigrationErrorReport
        val bucketName = migrationHelperDeploymentService.migrationS3BucketName
        report = try {
            s3UploadService!!.upload(pathToDatabaseFile, bucketName, uploadStageTransitionCallback)
        } catch (e: FileUploadException) {
            migrationService.error(e)
            throw DatabaseMigrationFailure("Error when uploading database dump to S3", e)
        }
        try {
            restoreService.restoreDatabase(restoreStageTransitionCallback)
        } catch (e: Exception) {
            migrationService.error(e)
            throw DatabaseMigrationFailure("Error when restoring database", e)
        }
        return report
    }

    val elapsedTime: Optional<Duration>
        get() {
            val start = startTime.get()
            return if (!start.isPresent) {
                Optional.empty()
            } else Optional.of(Duration.between(start.get(), LocalDateTime.now()))
        }

    fun scheduleMigration(): Boolean {
        val jobId = scheduledJobId
        val jobRunner = DatabaseMigrationJobRunner(this)
        val result = migrationRunner.runMigration(jobId, jobRunner)
        if (!result) {
            migrationService.error("Unable to start database migration job.")
        }
        return result
    }

    @Throws(InvalidMigrationStageError::class)
    fun abortMigration() {
        // We always try to remove scheduled job if the system is in inconsistent state
        migrationRunner.abortJobIfPresesnt(scheduledJobId)
        if (!migrationService.currentStage.isDBPhase || s3UploadService == null) {
            throw InvalidMigrationStageError(String.format("Invalid migration stage when cancelling filesystem migration: %s",
                    migrationService.currentStage))
        }
        logger.warn("Aborting running filesystem migration")
        migrationService.error("File system migration was aborted")
    }

    private val scheduledJobId: JobId
        private get() = JobId.of(DatabaseMigrationJobRunner.KEY + migrationService.currentMigration.id)

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseMigrationService::class.java)
    }

}
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
package com.atlassian.migration.datacenter.core.db

import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService
import com.atlassian.migration.datacenter.core.util.MigrationJobRunner
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.JobRunnerResponse
import org.slf4j.LoggerFactory
import java.security.Key
import java.util.concurrent.atomic.AtomicBoolean

class DatabaseMigrationJobRunner(private val databaseMigrationService: DatabaseMigrationService) : MigrationJobRunner {
    companion object {
        private val log = LoggerFactory.getLogger(DatabaseMigrationJobRunner::class.java)
        val KEY = DatabaseMigrationJobRunner::class.java.name
        private val isRunning = AtomicBoolean(false)
    }

    override val key: String
        get() = KEY

    override fun runJob(request: JobRunnerRequest): JobRunnerResponse {
        if (!isRunning.compareAndSet(false, true)) {
            return JobRunnerResponse.aborted("Database migration job is already running")
        }
        log.info("Starting database migration job")
        try {
            databaseMigrationService.performMigration()
        } catch (e: InvalidMigrationStageError) {
            log.error("Invalid migration transition - {}", e.message)
            return JobRunnerResponse.failed(e)
        } finally {
            isRunning.set(false)
        }
        log.info("Finished DB migration job")
        return JobRunnerResponse.success("Database migration complete")
    }

}
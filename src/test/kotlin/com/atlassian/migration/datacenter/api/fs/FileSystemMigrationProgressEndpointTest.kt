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
package com.atlassian.migration.datacenter.api.fs

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationReport
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import java.nio.file.Paths
import java.time.Duration
import java.util.HashSet
import javax.ws.rs.core.Response
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FileSystemMigrationProgressEndpointTest {
    @MockK
    lateinit var fsMigrationService: FilesystemMigrationService

    @MockK
    lateinit var report: FileSystemMigrationReport

    @InjectMockKs
    lateinit var endpoint: FileSystemMigrationEndpoint

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun shouldReturnReportWhenMigrationExists() {
        val testReason = "test reason"
        val testFile = Paths.get("file")
        val failedFileMigration = FailedFileMigration(testFile, testReason)
        val failedFilesCollection = hashSetOf<FailedFileMigration>()
        failedFilesCollection.add(failedFileMigration)
        every { fsMigrationService.getReport() } returns mockk {
            every { status } returns FilesystemMigrationStatus.RUNNING
            every { getNumberOfCommencedFileUploads() } returns 1L
            every { getNumberOfFilesFound() } returns 1L
            every { getFailedFiles() } returns failedFilesCollection
            every { getCountOfMigratedFiles() } returns 1L
            every { elapsedTime } returns Duration.ofMinutes(1)
        }
        val response = endpoint.getFilesystemMigrationStatus()
        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        val responseJson = response.entity as String
        val reader = mapper.reader()
        val tree = reader.readTree(responseJson)
        val responseStatus = tree.at("/status").asText()
        val responseReason = tree.at("/failedFiles/0/reason").asText()
        val responseFailedFile = tree.at("/failedFiles/0/filePath").asText()
        val responseSuccessFileCount = tree.at("/migratedFiles").asLong()
        Assertions.assertEquals(FilesystemMigrationStatus.RUNNING.name, responseStatus)
        Assertions.assertEquals(testReason, responseReason)
        Assertions.assertEquals(testFile.toUri().toString(), responseFailedFile)
        Assertions.assertEquals(1, responseSuccessFileCount)
    }

    @Test
    fun shouldHandleVeryLargeReport() {
        every { fsMigrationService.getReport() } returns report
        every { report.status } returns FilesystemMigrationStatus.RUNNING
        every { report.elapsedTime } returns Duration.ofMinutes(1)
        every { report.getNumberOfFilesFound() } returns 1000000L
        every { report.getNumberOfCommencedFileUploads() } returns 1000000L
        val failedFiles: MutableSet<FailedFileMigration> = HashSet()
        val testReason = "test reason"
        val testFile = Paths.get("file")
        for (i in 0..99) {
            val failedFileMigration = FailedFileMigration(testFile, testReason)
            failedFiles.add(failedFileMigration)
        }
        every { report.getFailedFiles() } returns failedFiles
        every { report.getCountOfMigratedFiles() } returns 1000000L
        val response = endpoint.getFilesystemMigrationStatus()
        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        val responseJson = response.entity as String
        val reader = mapper.reader()
        val tree = reader.readTree(responseJson)
        val responseStatus = tree.at("/status").asText()
        val responseReason = tree.at("/failedFiles/99/reason").asText()
        val responseFailedFile = tree.at("/failedFiles/99/filePath").asText()
        val responseSuccessFileCount = tree.at("/migratedFiles").asLong()
        Assertions.assertEquals(FilesystemMigrationStatus.RUNNING.name, responseStatus)
        Assertions.assertEquals(testReason, responseReason)
        Assertions.assertEquals(testFile.toUri().toString(), responseFailedFile)
        Assertions.assertEquals(1000000, responseSuccessFileCount)
    }

    @Test
    fun shouldReturnBadRequestWhenNoReportExists() {
        every { fsMigrationService.getReport() } returns DefaultFileSystemMigrationReport()
        val response = endpoint.getFilesystemMigrationStatus()
        Assertions.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
        MatcherAssert.assertThat<String?>(
            response.entity.toString(),
            Matchers.containsString("no file system migration exists")
        )
    }

    @Test
    fun shouldNotRunFileMigrationWhenExistingMigrationIsInProgress() {
        val reportMock = mockk<FileSystemMigrationReport>()
        every { reportMock.status } returns FilesystemMigrationStatus.RUNNING
        every { fsMigrationService.isRunning() } returns true
        every { fsMigrationService.getReport() } returns reportMock
        val response = endpoint.runFileMigration()
        Assertions.assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        Assertions.assertEquals(FilesystemMigrationStatus.RUNNING, (response.entity as MutableMap<*, *>)["status"])
    }

    @Test
    fun shouldRunFileMigrationWhenNoOtherMigrationIsNotInProgress() {
        every { fsMigrationService.isRunning() } returns false
        every { fsMigrationService.scheduleMigration() } returns true
        val response = endpoint.runFileMigration()
        Assertions.assertEquals(Response.Status.ACCEPTED.statusCode, response.status)
        Assertions.assertEquals(true, (response.entity as MutableMap<*, *>)["migrationScheduled"])
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotRunFileMigrationWhenWhenUnableToScheduleMigration() {
        every { fsMigrationService.isRunning() } returns false
        every { fsMigrationService.scheduleMigration() } returns false
        val response = endpoint.runFileMigration()
        Assertions.assertEquals(Response.Status.CONFLICT.statusCode, response.status)
        Assertions.assertEquals(false, (response.entity as MutableMap<*, *>)["migrationScheduled"])
    }
}
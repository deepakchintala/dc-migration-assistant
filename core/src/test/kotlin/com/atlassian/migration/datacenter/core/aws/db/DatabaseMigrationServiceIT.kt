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

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration
import com.atlassian.migration.datacenter.core.aws.db.restore.DatabaseRestoreStageTransitionCallback
import com.atlassian.migration.datacenter.core.aws.db.restore.SsmPsqlDatabaseRestoreService
import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi
import com.atlassian.migration.datacenter.core.db.DatabaseExtractorFactory.getExtractor
import com.atlassian.migration.datacenter.core.util.MigrationRunner
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.util.AwsCredentialsProviderShim
import com.atlassian.migration.datacenter.util.PSQLContainerHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse
import java.net.URI
import java.nio.file.Path
import java.util.concurrent.ExecutionException
import java.util.function.Supplier

@Tag("integration")
@Testcontainers
@ExtendWith(MockitoExtension::class)
internal class DatabaseMigrationServiceIT {
    companion object {
        @Container
        var postgres = PSQLContainerHelper("postgres:9.6")
                .withDatabaseName("jira")
                .withCopyFileToContainer(MountableFile.forClasspathResource("db/jira.sql"), "/docker-entrypoint-initdb.d/jira.sql")

        @Container
        var s3 = LocalStackContainer()
                .withServices(LocalStackContainer.Service.S3)
                .withEnv("DEFAULT_REGION", Region.US_EAST_1.toString())
    }

    private var s3client: S3AsyncClient? = null
    private val bucket = "trebuchet-testing"

    @Mock(lenient = true)
    var configuration: ApplicationConfiguration? = null

    @Mock
    var ssmApi: SSMApi? = null

    var tempDir: Path? = null

    @Mock(lenient = true)
    private val migrationService: MigrationService? = null

    @Mock
    private val migrationRunner: MigrationRunner? = null

    @Mock
    var migrationHelperDeploymentService: AWSMigrationHelperDeploymentService? = null

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        tempDir = createTempDir().toPath()
        Mockito.`when`(configuration!!.databaseConfiguration)
                .thenReturn(DatabaseConfiguration(DatabaseConfiguration.DBType.POSTGRESQL,
                        postgres.containerIpAddress,
                        postgres.getMappedPort(5432),
                        postgres.databaseName,
                        postgres.username,
                        postgres.password))
        s3client = S3AsyncClient.builder()
                .endpointOverride(URI(s3.getEndpointConfiguration(LocalStackContainer.Service.S3).serviceEndpoint))
                .credentialsProvider(AwsCredentialsProviderShim(s3.defaultCredentialsProvider))
                .region(Region.US_EAST_1)
                .build()
        Mockito.`when`(migrationHelperDeploymentService!!.migrationHostInstanceId).thenReturn("1-0123456789")
        Mockito.`when`(migrationHelperDeploymentService!!.dbRestoreDocument).thenReturn("ATL-RESTORE-DB")
        Mockito.`when`(migrationHelperDeploymentService!!.migrationS3BucketName).thenReturn(bucket)
        val req = CreateBucketRequest.builder()
                .bucket(bucket)
                .build()
        val resp = s3client!!.createBucket(req).get()
        Assertions.assertTrue(resp.sdkHttpResponse().isSuccessful)
    }

    @Test
    @Throws(ExecutionException::class, InterruptedException::class, InvalidMigrationStageError::class)
    fun testDatabaseMigration() {
        val databaseArchivalService = DatabaseArchivalService(getExtractor(configuration!!))
        val archiveStageTransitionCallback = DatabaseArchiveStageTransitionCallback(migrationService)
        val s3UploadService = DatabaseArtifactS3UploadService(Supplier { s3client })
        s3UploadService.postConstruct()
        val uploadStageTransitionCallback = DatabaseUploadStageTransitionCallback(migrationService)
        Mockito.`when`(ssmApi!!.runSSMDocument(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyMap())).thenReturn("my-commnd")
        Mockito.`when`(ssmApi!!.getSSMCommand(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(GetCommandInvocationResponse.builder().status(CommandInvocationStatus.SUCCESS).build())
        val restoreService = SsmPsqlDatabaseRestoreService(ssmApi, migrationHelperDeploymentService)
        val restoreStageTransitionCallback = DatabaseRestoreStageTransitionCallback(migrationService)
        val service = DatabaseMigrationService(tempDir!!,
                migrationService!!,
                migrationRunner!!,
                databaseArchivalService,
                archiveStageTransitionCallback,
                s3UploadService,
                uploadStageTransitionCallback,
                restoreService,
                restoreStageTransitionCallback, migrationHelperDeploymentService!!)
        val report = service.performMigration()
        val req = HeadObjectRequest.builder()
                .bucket(bucket)
                .key("db.dump/toc.dat")
                .build()
        val resp = s3client!!.headObject(req).get()
        Assertions.assertTrue(resp.sdkHttpResponse().isSuccessful)
        Assertions.assertEquals(0, report.failedFiles.size)
    }

}
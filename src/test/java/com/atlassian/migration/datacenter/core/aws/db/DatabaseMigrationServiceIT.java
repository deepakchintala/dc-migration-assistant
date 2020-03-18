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

package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.util.AwsCredentialsProviderShim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Tag("integration")
@Testcontainers
@ExtendWith(MockitoExtension.class)
class DatabaseMigrationServiceIT
{
    @Container
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
        .withDatabaseName("jira")
        .withCopyFileToContainer(MountableFile.forClasspathResource("db/jira.sql"), "/docker-entrypoint-initdb.d/jira.sql");

    @Container
    public LocalStackContainer s3 = new LocalStackContainer()
        .withServices(S3)
        .withEnv("DEFAULT_REGION", Region.US_EAST_1.toString());

    private S3AsyncClient s3client;
    private String bucket = "trebuchet-testing";

    @Mock(lenient = true)
    ApplicationConfiguration configuration;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        when(configuration.getDatabaseConfiguration())
            .thenReturn(new DatabaseConfiguration(DatabaseConfiguration.DBType.POSTGRESQL,
                                                  postgres.getContainerIpAddress(),
                                                  postgres.getMappedPort(5432),
                                                  postgres.getDatabaseName(),
                                                  postgres.getUsername(),
                                                  postgres.getPassword()));

        s3client = S3AsyncClient.builder()
            .endpointOverride(new URI(s3.getEndpointConfiguration(S3).getServiceEndpoint()))
            .credentialsProvider(new AwsCredentialsProviderShim(s3.getDefaultCredentialsProvider()))
            .region(Region.US_EAST_1)
            .build();

        CreateBucketRequest req = CreateBucketRequest.builder()
            .bucket(bucket)
            .build();
        CreateBucketResponse resp = s3client.createBucket(req).get();
        assertTrue(resp.sdkHttpResponse().isSuccessful());
    }


    @Test
    void testDatabaseMigration() throws ExecutionException, InterruptedException {
        DatabaseMigrationService service = new DatabaseMigrationService(configuration, tempDir, () -> s3client);
        service.postConstruct();

        service.performMigration();

        HeadObjectRequest req = HeadObjectRequest.builder()
            .bucket(bucket)
            .key("db.dump/toc.dat")
            .build();
        HeadObjectResponse resp = s3client.headObject(req).get();
        assertTrue(resp.sdkHttpResponse().isSuccessful());
    }

}
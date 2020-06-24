package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import java.net.URI;
import static org.mockito.Mockito.*;

/*
 * TODO: These tests require localstack to be running before hand. Unfortunately the standard way we run localstack from the tests themselves
 * is not exposing the EC2 port 4592 (EC2) and so we get connection refused errors. As a workaround manually run localstack as follows
 * 
 * docker run -p 4597:4597 -e SERVICES=ec2 -e DEFAULT_REGION=us-east-1 localstack/localstack
 * 
 * Run in this way 4597 accepts incoming requests and the tests pass.
 */

@ExtendWith({MockitoExtension.class})
@Disabled
class RemoteInstanceCommandRunnerServiceTest {
    private static final String LOCAL_EC2_ENDPOINT = "http://localhost:4597";
    
//TODO: Tried to replicate "docker run -p 4597:4597 -e SERVICES=ec2 -e DEFAULT_REGION=us-east-1 localstack/localstack" via the code below, however we still
//encounter connection refused errors on 4592    
//    private static Map<String, String> envs  = new HashMap<String, String>() {{
//        put("SERVICES", "ec2");
//        put("DEFAULT_REGION", "us-east-1");
//    }};
//
//    private static final GenericContainer AwsEc2Instance = new GenericContainer("localstack/localstack:0.11.2")
//            .withExposedPorts(4597)
//            .withEnv(envs);
    @Mock
    MigrationContext migrationContext;

    @Mock
    MigrationService migrationService;

    @Mock
    private AwsCredentialsProvider mockCredentialsProvider;

    @Mock
    SSMApi ssmApi;

    private Ec2Client ec2Client;

    private RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService;

    @BeforeEach
    public void setUp() {
//        AwsEc2Instance.start();

        ec2Client = Ec2Client.builder()
                .credentialsProvider(mockCredentialsProvider)
                .endpointOverride(URI.create(LOCAL_EC2_ENDPOINT))
                .region(Region.US_EAST_1)
                .build();

        when(mockCredentialsProvider.resolveCredentials()).thenReturn(new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return "fake-access-key";
            }

            @Override
            public String secretAccessKey() {
                return "fake-secret-key";
            }
        });
        createEC2Instance(ec2Client);
    }

    @Test
    public void shouldCallRestartOnStackInstances() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn("JIRA_STACK_001");
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(1)).runSSMDocument(anyString(), anyString(), any());
    }
    
    @Test
    public void shouldNotCallRestartOnStackInstances() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn("NON_EXISTENT_STACK");
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    public void  createEC2Instance(Ec2Client ec2) {

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId("ami-031a03cb800ecb0d5")
                .instanceType(InstanceType.T1_MICRO)
                .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        Tag tag = Tag.builder()
                .key("tag:aws:cloudformation:stack-name")
                .value("JIRA_STACK_001")
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        ec2.createTags(tagRequest);
    }
}
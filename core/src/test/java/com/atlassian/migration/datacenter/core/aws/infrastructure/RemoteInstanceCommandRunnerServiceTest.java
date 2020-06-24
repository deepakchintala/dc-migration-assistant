package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.DockerComposeContainer;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.io.File;
import java.net.URI;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class RemoteInstanceCommandRunnerServiceTest {

    /*
     * Unfortunately the standard way we run localstack from the tests (@LocalstackDockerProperties) themselves is not 
     * exposing the EC2 port 4592 (EC2) and so we get connection refused errors. As a workaround start localstack using 
     * docker-compose before running the tests
     *
     * Run in this way 4597 accepts incoming requests and the tests pass.
     */
    public static DockerComposeContainer localstackForEc2 =
            new DockerComposeContainer(new File("src/test/java/com/atlassian/migration/datacenter/core/util/docker/docker-compose.yml"));
    
    private static final String LOCAL_EC2_ENDPOINT = "http://localhost:4597";
    public static final String JIRA_STACK_NAME = "JIRA_STACK_001";

    @Mock
    MigrationContext migrationContext;

    @Mock
    MigrationService migrationService;

    @Mock
    private AwsCredentialsProvider mockCredentialsProvider;

    @Mock
    SSMApi ssmApi;

    @Mock
    private Ec2Client ec2Client;

    private RemoteInstanceCommandRunnerService remoteInstanceCommandRunnerService;

    @BeforeEach
    public void setUp() {
        localstackForEc2.start();
        
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
    public void shouldCallRestartOnStackInstance() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(1)).runSSMDocument(anyString(), anyString(), any());
    }
    
    @Test
    public void shouldNotCallRestartOnStackInstanceIfMigrationStackNotFound() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn("NOT_JIRA_STACK");
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformRestartIfNoEc2ClientAvailable() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> null);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(JIRA_STACK_NAME);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformRestartIfMigrationContextIsNull() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(null);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    @Test
    public void shouldNotPerformRestartIfDeploymentIdIsNull() throws S3SyncFileSystemDownloader.CannotLaunchCommandException {
        remoteInstanceCommandRunnerService = new RemoteInstanceCommandRunnerService(ssmApi, migrationService, () -> ec2Client);
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn(null);
        remoteInstanceCommandRunnerService.restartJiraService();
        verify(ssmApi, times(0)).runSSMDocument(anyString(), anyString(), any());
    }

    private void createEC2Instance(Ec2Client ec2) {

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
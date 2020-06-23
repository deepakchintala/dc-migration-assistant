package com.atlassian.migration.datacenter.core.aws.infrastructure;

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.util.AwsCredentialsProviderShim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.net.URI;

import static org.mockito.Mockito.when;

@ExtendWith({LocalstackDockerExtension.class, MockitoExtension.class})
@LocalstackDockerProperties(services = {"ec2"}, imageTag = "0.10.8")
class ApplicationRestartServiceTest {

    @Mock
    MigrationContext migrationContext;

    @Mock
    MigrationService migrationService;

    @Mock
    SSMApi ssmApi;

    private Ec2Client client;

    private ApplicationRestartService sut;

    @BeforeEach
    public void setUp() {
        client = Ec2Client.builder()
                .endpointOverride(URI.create("http://localhost:4597"))
                .region(Region.of(TestUtils.DEFAULT_REGION))
                .credentialsProvider(new AwsCredentialsProviderShim(TestUtils.getCredentialsProvider()))
                .build();
        sut = new ApplicationRestartService(ssmApi, migrationService, () -> client);
    }

    @Test
    public void shouldCallRestartOnStackInstances() {
        when(migrationService.getCurrentContext()).thenReturn(migrationContext);
        when(migrationContext.getApplicationDeploymentId()).thenReturn("DUMMY_STACK_001");
        client.runInstances(builder -> builder.tagSpecifications(tagBuilder -> {
            tagBuilder.tags(tag -> {
                tag.key("aws:cloudformation:stack-name").value("DUMMY_STACK_001");
            });
        }));
        sut.restartJiraService();
    }
}
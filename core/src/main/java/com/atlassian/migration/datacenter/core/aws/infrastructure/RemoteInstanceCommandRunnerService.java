package com.atlassian.migration.datacenter.core.aws.infrastructure;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

public class RemoteInstanceCommandRunnerService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteInstanceCommandRunnerService.class);
    public static final String AWS_RUN_SHELL_SCRIPT = "AWS-RunShellScript";

    private final SSMApi ssm;
    private final MigrationService migrationService;
    private final Supplier<Ec2Client> ec2ClientSupplier;

    public RemoteInstanceCommandRunnerService(SSMApi ssm, MigrationService migrationService, Supplier<Ec2Client> ec2ClientSupplier) {
        this.ssm = ssm;
        this.migrationService = migrationService;
        this.ec2ClientSupplier = ec2ClientSupplier;
    }
    
    public void restartJiraService() {
        String stackName = migrationService.getCurrentContext().getApplicationDeploymentId();
        Ec2Client ec2Client = ec2ClientSupplier.get();
        DescribeInstancesResponse ec2InstanceMetaData = describeInstances(stackName, ec2Client);
        Optional<String> instanceId = getInstanceId(ec2InstanceMetaData);
        if(instanceId.isPresent()) {
            try {
                logger.info(String.format("Restarting Jira instance [%s] now...", instanceId.get()));
                ssm.runSSMDocument(AWS_RUN_SHELL_SCRIPT, instanceId.get(),
                        ImmutableMap.of("commands", Collections.singletonList("sudo systemctl restart jira")));
            } catch (S3SyncFileSystemDownloader.CannotLaunchCommandException e) {
                logger.error(String.format("Failed to restart Jira instance [%s] with exception: %s", instanceId.get(), e));
            }
        } else {
            logger.info(String.format("Could not find a Jira instance to restart for the stack [%s]", stackName));
        }
    }

    private Optional<String> getInstanceId(DescribeInstancesResponse ec2Instance) {
        return ec2Instance.reservations()
                    .stream()
                    .map(Reservation::instances)
                    .flatMap(Collection::stream)
                    .map(Instance::instanceId)
                    .findFirst();
    }

    private DescribeInstancesResponse describeInstances(String stackName, Ec2Client ec2Client) {
        return ec2Client.describeInstances(builder -> {
                builder.filters(Filter.builder()
                        .name("tag:aws:cloudformation:stack-name")
                        .values(stackName)
                        .build()
                );
            });
    }
}

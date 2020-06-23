package com.atlassian.migration.datacenter.core.aws.infrastructure;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.exceptions.JiraRestartFailure;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.util.Collections;
import java.util.function.Supplier;

public class ApplicationRestartService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRestartService.class);
    
    private final SSMApi ssm;
    private final MigrationService migrationService;
    private final Supplier<Ec2Client> ec2ClientSupplier;

    public ApplicationRestartService(SSMApi ssm, MigrationService migrationService, Supplier<Ec2Client> ec2ClientSupplier) {
        this.ssm = ssm;
        this.migrationService = migrationService;
        this.ec2ClientSupplier = ec2ClientSupplier;
    }
    
    public void restartJiraService() throws JiraRestartFailure {
        String stackName = migrationService.getCurrentContext().getApplicationDeploymentId();

        Ec2Client ec2Client = ec2ClientSupplier.get();
        DescribeInstancesResponse response = ec2Client.describeInstances(builder -> {
            builder.filters(Filter.builder()
                    .name("tag:aws:cloudformation:stack-name")
                    .values(stackName)
                    .build()
            );
        });

        if(response.hasReservations()) {
            for(Reservation reservation : response.reservations()) {
                for(Instance instance : reservation.instances()) {
                    try {
                        ssm.runSSMDocument("AWS-RunShellScript", instance.instanceId(),
                                ImmutableMap.of("commands", Collections.singletonList("sudo systemctl restart jira")));
                    } catch (S3SyncFileSystemDownloader.CannotLaunchCommandException e) {
                        throw new JiraRestartFailure("unable to restart Jira instance using AWS-RunShellScript SSM playbook");
                    }
                }
            }
        }
    }
}

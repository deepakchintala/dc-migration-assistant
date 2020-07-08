package com.atlassian.migration.datacenter.core.aws.infrastructure.util;

import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class AwsResourceManager {

    public static Ec2Client getEc2Client(Supplier<Ec2Client> ec2ClientSupplier) {
        return Optional.of(ec2ClientSupplier)
                .map(Supplier<Ec2Client>::get)
                .orElse(null);
    }

    public static String getStackName(MigrationService migrationService) {
        return Optional.of(migrationService)
                .map(MigrationService::getCurrentContext)
                .map(MigrationContext::getApplicationDeploymentId)
                .orElse(null);
    }

    public static Optional<String> getInstanceId(DescribeInstancesResponse ec2Instance) {
        return ec2Instance.reservations()
                .stream()
                .map(Reservation::instances)
                .flatMap(Collection::stream)
                .map(Instance::instanceId)
                .findFirst();
    }

    public static DescribeInstancesResponse describeInstances(String stackName, Ec2Client ec2Client) throws AwsServiceException, SdkClientException {
        return ec2Client.describeInstances(builder -> {
            builder.filters(Filter.builder()
                    .name("tag-key")
                    .values("tag:aws:cloudformation:stack-name")
                    .name("tag-value")
                    .values(stackName)
                    .build()
            );
        });
    }
}

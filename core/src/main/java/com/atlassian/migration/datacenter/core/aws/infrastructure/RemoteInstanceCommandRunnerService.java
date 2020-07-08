package com.atlassian.migration.datacenter.core.aws.infrastructure;
import com.atlassian.migration.datacenter.core.aws.db.restore.JiraState;
import com.atlassian.migration.datacenter.core.aws.infrastructure.util.AwsResourceManager;
import com.atlassian.migration.datacenter.core.aws.ssm.SSMApi;
import com.atlassian.migration.datacenter.core.fs.download.s3sync.S3SyncFileSystemDownloader;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

public class RemoteInstanceCommandRunnerService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteInstanceCommandRunnerService.class);
    private static final String AWS_RUN_SHELL_SCRIPT = "AWS-RunShellScript";
    public static final String SYSTEMCTL_START_JIRA = "sudo systemctl start jira";
    public static final String SYSTEMCTL_STOP_JIRA = "sudo systemctl stop jira";
    private final SSMApi ssm;
    private final MigrationService migrationService;
    private final Supplier<Ec2Client> ec2ClientSupplier;
    
    public RemoteInstanceCommandRunnerService(SSMApi ssm, MigrationService migrationService, Supplier<Ec2Client> ec2ClientSupplier) {
        this.ssm = ssm;
        this.migrationService = migrationService;
        this.ec2ClientSupplier = ec2ClientSupplier; 
    }
    
    public void setJiraRunStateTo(JiraState jiraState) {
        String logMessageState = jiraState.equals(JiraState.START) ? "start" : "stop";
        String stackName = AwsResourceManager.getStackName(migrationService);
        Ec2Client ec2Client = AwsResourceManager.getEc2Client(ec2ClientSupplier);
        if(stackName != null && ec2Client != null) {
            try {
                DescribeInstancesResponse ec2InstanceMetaData = AwsResourceManager.describeInstances(stackName, ec2Client);
                Optional<String> instanceId = AwsResourceManager.getInstanceId(ec2InstanceMetaData);
                if (instanceId.isPresent()) {
                    try {
                        logger.info(String.format("[%s] Jira instance [%s] on stack [%s] now...", logMessageState, instanceId.get(), stackName));
                        ssm.runSSMDocument(AWS_RUN_SHELL_SCRIPT, instanceId.get(),
                                ImmutableMap.of("commands", Collections.singletonList(jiraState.equals(JiraState.START) 
                                        ? SYSTEMCTL_START_JIRA 
                                        : SYSTEMCTL_STOP_JIRA)));
                    } catch (S3SyncFileSystemDownloader.CannotLaunchCommandException e) {
                        logger.error(String.format("Failed to [%s] Jira instance [%s] on stack [%s]", logMessageState, instanceId.get(), stackName), e);
                    }
                } else {    
                    logger.error(String.format("No Jira instance found to [%s] on stack [%s]", logMessageState, stackName));
                }
            } catch(AwsServiceException | SdkClientException e) {
                logger.error(String.format("Jira [%s] not possible for stack [%s]. Problem encountered when trying to obtain EC2 meta data:", logMessageState, stackName), e);
            }
        } else {
            logger.error(String.format("Jira [%s] not possible. No value for stack name and or no EC2 client present", logMessageState));
        }
    }
}

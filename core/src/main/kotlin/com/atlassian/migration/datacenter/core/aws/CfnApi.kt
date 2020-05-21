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
package com.atlassian.migration.datacenter.core.aws

import com.atlassian.migration.datacenter.core.aws.region.RegionService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient
import software.amazon.awssdk.services.cloudformation.model.*
import software.amazon.awssdk.services.cloudformation.model.Stack
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException
import java.util.function.Consumer
import java.util.stream.Collectors

class CfnApi(private var credentialsProvider: AwsCredentialsProvider? = null, private var regionManager: RegionService? = null) {
    /**
     * Stored client is only used for testing purposes
     */
    var clientOverride: CloudFormationAsyncClient? = null

    /**
     * Package private constructor to consume a CFn Async Client. Currently used for testing.
     * his will not be called by spring as no injectable `CloudFormationAsyncClient` instance exists in the container.
     *
     * @param client An async CloudFormation client
     */
    internal constructor(client: CloudFormationAsyncClient): this(null, null) {
        this.clientOverride = client
    }

    /**
     * Return a client should only be called after necessary AWS information has been provided.
     */
    private fun getClient(): CloudFormationAsyncClient {
        return clientOverride ?: CloudFormationAsyncClient.builder()
                        .credentialsProvider(credentialsProvider)
                        .region(Region.of(regionManager!!.region))
                        .build()
    }

    fun getStatus(stackName: String?): InfrastructureDeploymentStatus {
        val stack = getStack(stackName)
        if (!stack.isPresent) {
            throw StackInstanceNotFoundException
                    .builder()
                    .message(String.format("Stack with name %s not found", stackName))
                    .build()
        }
        val theStack = stack.get()
        val deploymentState: InfrastructureDeploymentState
        deploymentState = when (theStack.stackStatus()) {
            StackStatus.CREATE_COMPLETE -> InfrastructureDeploymentState.CREATE_COMPLETE
            StackStatus.CREATE_IN_PROGRESS -> InfrastructureDeploymentState.CREATE_IN_PROGRESS
            else -> InfrastructureDeploymentState.CREATE_FAILED
        }
        return InfrastructureDeploymentStatus(deploymentState, theStack.stackStatusReason())
    }

    fun provisionStack(templateUrl: String, stackName: String?, params: Map<String, String>): Optional<String> {
        logger.trace("received request to create stack {} from template {}", stackName, templateUrl)
        val parameters = params.entries
                .stream()
                .map { e: Map.Entry<String, String> -> Parameter.builder().parameterKey(e.key).parameterValue(e.value).build() }
                .collect(Collectors.toSet())
        val tag = Tag.builder()
                .key("created_by")
                .value("atlassian-dcmigration")
                .build()
        val createStackRequest = CreateStackRequest.builder()
                .templateURL(templateUrl)
                .capabilities(Capability.CAPABILITY_AUTO_EXPAND, Capability.CAPABILITY_IAM)
                .stackName(stackName)
                .parameters(parameters)
                .tags(tag)
                .build()

        return try {
            val response = getClient()
                    .createStack(createStackRequest)
                    .get()

            if (!response.sdkHttpResponse().isSuccessful) {
                logger.error("create stack {} http response failed with reason: {}", stackName, response.sdkHttpResponse().statusText())
                return Optional.empty()
            }
            logger.info("stack {} creation succeeded", stackName)
            Optional.ofNullable(response.stackId())

        } catch (e: InterruptedException) {
            logger.error("Error deploying cloudformation stack {}", stackName, e)
            Optional.empty()

        } catch (e: ExecutionException) {
            logger.error("Error deploying cloudformation stack {}", stackName, e)
            Optional.empty()
        }
    }

    /**
     * Gets all Cloudformation exports. If there is an error retrieving the exports, an empty map will be returned
     *
     * @return A map (of export name to export value) containing all cloudformation exports for the current region in the current account.
     */
    val exports: Map<String, String>
        get() {
            val asyncResponse = getClient().listExports()
            return try {
                val response = asyncResponse.get()
                val exportsMap = HashMap<String, String>()
                response.exports().forEach(Consumer { export: Export -> exportsMap[export.name()] = export.value() })
                exportsMap
            } catch (e: InterruptedException) {
                logger.error("Unable to get cloudformation exports", e)
                emptyMap()
            } catch (e: ExecutionException) {
                logger.error("Unable to get cloudformation exports", e)
                emptyMap()
            }
        }

    /**
     * Gets all resources for the given stack. If there is an error retrieving the resources, an empty map will be returned
     *
     * @param stackName the name of the stack to get the resources of
     * @return a map of the logical resource ID to the resource for all resources in the given stack
     */
    fun getStackResources(stackName: String?): Map<String, StackResource> {
        val request = DescribeStackResourcesRequest.builder()
                .stackName(stackName)
                .build()
        val asyncResponse = getClient().describeStackResources(request)
        return try {
            val response = asyncResponse.get()
            val resources: MutableMap<String, StackResource> = HashMap()
            response.stackResources().forEach(Consumer { resource: StackResource -> resources[resource.logicalResourceId()] = resource })
            resources
        } catch (e: InterruptedException) {
            logger.error("Error getting stack {} resources", stackName, e)
            emptyMap()
        } catch (e: ExecutionException) {
            logger.error("Error getting stack {} resources", stackName, e)
            emptyMap()
        }
    }

    fun getStack(stackName: String?): Optional<Stack> {
        val request = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build()
        val asyncResponse = getClient()
                .describeStacks(request)
        return try {
            val response = asyncResponse.join()
            val stack = response.stacks()[0]
            Optional.ofNullable(stack)
        } catch (e: CompletionException) {
            logger.error("Error getting stack {}", stackName, e)
            Optional.empty()
        } catch (e: CancellationException) {
            logger.error("Error getting stack {}", stackName, e)
            Optional.empty()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CfnApi::class.java)
    }
}
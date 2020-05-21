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
package com.atlassian.migration.datacenter.core.aws.infrastructure

import com.atlassian.migration.datacenter.core.aws.CfnApi
import com.atlassian.migration.datacenter.core.aws.infrastructure.CloudformationDeploymentService
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Superclass for classes which manage the deployment of cloudformation
 * templates.
 */
abstract class CloudformationDeploymentService @JvmOverloads internal constructor(private val cfnApi: CfnApi, private val deployStatusPollIntervalSeconds: Int = 30) {
    private var deploymentWatcher: ScheduledFuture<*>? = null

    /**
     * Method that will be called if the deployment
     * [this.deployCloudformationStack] fails
     */
    protected abstract fun handleSuccessfulDeployment()

    /**
     * Method that will be called if the deployment succeeds
     */
    protected abstract fun handleFailedDeployment(message: String)

    /**
     * Deploys a cloudformation stack and starts a thread to monitor the deployment.
     *
     * @param templateUrl the S3 url of the cloudformation template to deploy
     * @param stackName   the name for the cloudformation stack
     * @param params      the parameters for the cloudformation template
     */
    fun deployCloudformationStack(templateUrl: String, stackName: String, params: Map<String, String>) {
        cfnApi.provisionStack(templateUrl, stackName, params)
        beginWatchingDeployment(stackName)
    }

    fun getDeploymentStatus(stackName: String): InfrastructureDeploymentStatus {
        Objects.requireNonNull(stackName)
        val status = cfnApi.getStatus(stackName)
        if (status.state == InfrastructureDeploymentState.CREATE_FAILED) {
            logger.error("discovered that cloudformation stack deployment failed when getting status. Reason is: {}", status.reason)
            handleFailedDeployment(status.reason)
            deploymentWatcher!!.cancel(true)
        }
        return status
    }

    private fun beginWatchingDeployment(stackName: String) {
        val stackCompleteFuture = CompletableFuture<String>()
        val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

        deploymentWatcher = scheduledExecutorService.scheduleAtFixedRate({
            val status = cfnApi.getStatus(stackName)
            if (status.state == InfrastructureDeploymentState.CREATE_COMPLETE) {
                logger.info("stack {} creation succeeded", stackName)
                handleSuccessfulDeployment()
                stackCompleteFuture.complete("")
            }
            if (status.state == InfrastructureDeploymentState.CREATE_FAILED) {
                logger.error("stack {} creation failed with reason {}", stackName, status.reason)
                handleFailedDeployment(status.reason)
                stackCompleteFuture.complete("")
            }
        }, 0, deployStatusPollIntervalSeconds.toLong(), TimeUnit.SECONDS)

        val canceller = scheduledExecutorService.scheduleAtFixedRate({
            if (deploymentWatcher!!.isCancelled()) {
                return@scheduleAtFixedRate
            }
            val message = String.format("timed out while waiting for stack %s to deploy", stackName)
            logger.error(message)
            handleFailedDeployment(message)
            deploymentWatcher!!.cancel(true)
        }, 1, 100, TimeUnit.HOURS)

        stackCompleteFuture.whenComplete { result: String, thrown: Throwable ->
            deploymentWatcher!!.cancel(true)
            canceller.cancel(true)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CloudformationDeploymentService::class.java)
    }

}
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
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentState
import com.atlassian.migration.datacenter.spi.infrastructure.InfrastructureDeploymentStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class CloudformationDeploymentServiceTest {
    @Mock
    var mockCfnApi: CfnApi? = null
    var sut: CloudformationDeploymentService? = null
    var deploymentFailed = false
    var deploymentSucceeded = false

    @BeforeEach
    fun setup() {
        sut = object : CloudformationDeploymentService(mockCfnApi!!) {
            override fun handleFailedDeployment(message: String) {
                deploymentFailed = true
            }

            override fun handleSuccessfulDeployment() {
                deploymentSucceeded = true
            }
        }
    }

    @Test
    fun shouldDeployQuickStart() {
        deploySimpleStack()
        Mockito.verify(mockCfnApi)!!.provisionStack(TEMPLATE_URL, STACK_NAME, STACK_PARAMS)
    }

    @Test
    @Throws(InvalidMigrationStageError::class)
    fun shouldReturnInProgressWhileDeploying() {
        Mockito.`when`(mockCfnApi!!.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentStatus(InfrastructureDeploymentState.CREATE_IN_PROGRESS, ""))
        deploySimpleStack()
        val status = sut!!.getDeploymentStatus(STACK_NAME)
        Assertions.assertEquals(InfrastructureDeploymentState.CREATE_IN_PROGRESS, status.state)
        Assertions.assertEquals("", status.reason)
    }

    @Test
    @Throws(InterruptedException::class)
    fun shouldCallHandleFailedDeploymentWhenDeploymentFails() {
        val badStatus = "it broke"
        Mockito.`when`(mockCfnApi!!.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentStatus(InfrastructureDeploymentState.CREATE_FAILED, badStatus))
        deploySimpleStack()
        Thread.sleep(100)
        Assertions.assertTrue(deploymentFailed)
        Assertions.assertFalse(deploymentSucceeded)
        val status = sut!!.getDeploymentStatus(STACK_NAME)
        Assertions.assertEquals(InfrastructureDeploymentState.CREATE_FAILED, status.state)
        Assertions.assertEquals(badStatus, status.reason)
    }

    @Test
    @Throws(InterruptedException::class)
    fun shouldCallHandleSuccessfulDeploymentWhenDeploymentFails() {
        Mockito.`when`(mockCfnApi!!.getStatus(STACK_NAME)).thenReturn(InfrastructureDeploymentStatus(InfrastructureDeploymentState.CREATE_COMPLETE, ""))
        deploySimpleStack()
        Thread.sleep(100)
        Assertions.assertTrue(deploymentSucceeded)
        Assertions.assertFalse(deploymentFailed)
    }

    private fun deploySimpleStack() {
        sut!!.deployCloudformationStack(TEMPLATE_URL, STACK_NAME, STACK_PARAMS)
    }

    companion object {
        const val TEMPLATE_URL = "https://fake-url.com"
        const val STACK_NAME = "test-stack"
        val STACK_PARAMS = emptyMap<String, String>()
    }
}
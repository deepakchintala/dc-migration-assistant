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
package com.atlassian.migration.datacenter.api.aws

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure
import com.atlassian.migration.datacenter.core.aws.infrastructure.AtlassianInfrastructureService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.cloudformation.model.Output
import software.amazon.awssdk.services.cloudformation.model.Parameter
import software.amazon.awssdk.services.cloudformation.model.Stack
import javax.ws.rs.core.Response

@ExtendWith(MockKExtension::class)
class AWSGlobalInfrastructureEndpointTest {
    @MockK
    lateinit var mockGlobalInfrastructure: GlobalInfrastructure
    @MockK
    lateinit var mockAIS: AtlassianInfrastructureService

    @InjectMockKs
    lateinit var sut: AWSGlobalInfrastructureEndpoint

    @BeforeEach
    fun init() = MockKAnnotations.init(this)

    @Test
    fun itShouldReturnServerErrorWhenGlobalInfrastructureModuleFails() {
        every { mockGlobalInfrastructure.regions } returns null

        val res = sut.getRegions()

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.statusCode, res.status)
    }

    @Test
    fun itShouldReturnAllRegions() {
        val regionOne = "atlassian-east-1"
        val regionTwo = "atlassian-west-1"
        every { mockGlobalInfrastructure.regions } returns listOf(regionOne, regionTwo)

        val res = sut.getRegions()

        assertEquals(Response.Status.OK.statusCode, res.status)
        assertEquals(listOf(regionOne, regionTwo), res.entity)
    }


    val vpcid = Output.builder()
            .outputKey("VPCID")
            .outputValue("vpcval")
            .build()
    val privatesn = Output.builder()
            .outputKey("PrivateSubnets")
            .outputValue("privval")
            .build()
    val publicsn = Output.builder()
            .outputKey("PublicSubnets")
            .outputValue("pubval")
            .build()
    val export = Parameter.builder()
            .parameterKey("ExportPrefix")
            .parameterValue("prefix")
            .build()

    @Test
    fun itShouldConvertOutputsAndParams() {
        every { mockAIS.findASIs() } returns listOf(
                Stack.builder()
                        .stackName("stackname")
                        .stackId("stackid")
                        .outputs(vpcid, privatesn, publicsn)
                        .parameters(export)
                        .build()
        )

        val res = sut.getAvailableASIs()
        assertEquals(200, res.status)

        val asis = res.entity as List<Map<String, String>>
        assertEquals(1, asis.count())
        val asi = asis[0]
        assertEquals("prefix", asi["prefix"])
        assertEquals("stackname", asi["name"])
        assertEquals("stackid", asi["id"])
    }
}
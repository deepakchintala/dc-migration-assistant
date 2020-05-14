package com.atlassian.migration.datacenter

import com.amazonaws.util.ValidationUtils.assertNotEmpty
import com.atlassian.migration.datacenter.properties.Ec2Properties
import com.atlassian.migration.datacenter.services.TestService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNotNull


@ExtendWith(SpringExtension::class)
@ComponentScan(basePackages = ["com.atlassian.migration.datacenter"])
@EnableAutoConfiguration
class InterceptorTest {

    @Autowired
    lateinit var testService: TestService

    @Autowired
    lateinit var ec2Properties: Ec2Properties

    @Test
    fun testGetAmiId() {
        val amiId = testService.getAmiId()
        assertTrue { amiId == ec2Properties.amiId }
    }

    @Test
    fun testGetLaunchIndex() {
        val amiLaunchIndex = testService.getAmiLaunchIndex()
        assertTrue { amiLaunchIndex == ec2Properties.launchIndex }
    }

    @Test
    fun testGetAmiManifestPath() {
        val amiManifestPath = testService.getAmiManifestPath()
        assertTrue { amiManifestPath == ec2Properties.amiManifestPath }
    }

    @Test
    fun testGetAncestorAmiIds() {
        val ancestorAmiIds = testService.getAncestorAmiIds()
        assertTrue { ancestorAmiIds == ec2Properties.ancestorAmiIds }
    }

    @Test
    fun testGetAvailabilityZone() {
        val availabilityZone = testService.getAvailabilityZone()
        assertTrue { availabilityZone == ec2Properties.availabilityZone }
    }

    @Test
    fun testGetBlockDeviceMapping() {
        val blockMappings = testService.getBlockDeviceMapping()
        assertTrue { blockMappings == ec2Properties.blockDeviceMapping }
    }

    @Test
    fun testGetEC2InstanceRegion() {
        val region = testService.getEC2InstanceRegion()
        assertTrue(region == ec2Properties.ec2InstanceRegion)
    }

    @Test
    fun testGetHostAddressForEC2MetadataService() {
        val hostAddress = testService.getHostAddressForEC2MetadataService()
        assertTrue { hostAddress == ec2Properties.hostAddressForEC2MetadataService }
    }

    @Test
    fun testGetIAMInstanceProfileInfo() {
        val instanceProfileInfo = testService.getIAMInstanceProfileInfo()
        Assertions.assertAll(
                Executable { assertTrue(instanceProfileInfo.message == ec2Properties.iamInstanceProfileInfo["message"]) },
                Executable { assertTrue(instanceProfileInfo.lastUpdated == ec2Properties.iamInstanceProfileInfo["lastUpdated"]) },
                Executable { assertTrue(instanceProfileInfo.instanceProfileId == ec2Properties.iamInstanceProfileInfo["instanceProfileId"]) },
                Executable { assertTrue(instanceProfileInfo.instanceProfileArn == ec2Properties.iamInstanceProfileInfo["instanceProfileArn"]) },
                Executable { assertTrue(instanceProfileInfo.code == ec2Properties.iamInstanceProfileInfo["code"]) }
        )
    }

    @Test
    fun testGetIAMSecurityCredentials() {
        val credentials = testService.getIAMSecurityCredentials()
        Assertions.assertAll(
                Executable { assertNotNull(credentials) },
                Executable { assertTrue(credentials.isNotEmpty()) },
                Executable { assertTrue(credentials.containsKey("test")) }
        )
    }

    @Test
    fun testGetInstanceAction() {
        val action = testService.getInstanceAction()
        assertTrue { action == ec2Properties.instanceAction }
    }

    @Test
    fun testGetInstanceId() {
        val id = testService.getInstanceId()
        assertTrue { id == ec2Properties.instanceId }
    }

    @Test
    fun testGetInstanceInfo() {
        val instanceInfo = testService.getInstanceInfo()
        Assertions.assertAll(
                Executable { assertTrue(instanceInfo.accountId == ec2Properties.instanceInfo["accountId"]) },
                Executable { assertTrue(instanceInfo.architecture == ec2Properties.instanceInfo["architecture"]) },
                Executable { assertTrue(instanceInfo.availabilityZone == ec2Properties.instanceInfo["availabilityZone"]) },
                Executable { assertTrue(instanceInfo.billingProducts.size == ec2Properties.instanceInfo["billingProducts"]!!.split(",").toTypedArray().size) },
                Executable { assertTrue(instanceInfo.devpayProductCodes.size == ec2Properties.instanceInfo["devpayProductCodes"]!!.split(",").toTypedArray().size) },
                Executable { assertTrue(instanceInfo.imageId == ec2Properties.instanceInfo["imageId"]) },
                Executable { assertTrue(instanceInfo.instanceId == ec2Properties.instanceInfo["instanceId"]) },
                Executable { assertTrue(instanceInfo.instanceType == ec2Properties.instanceInfo["instanceType"]) },
                Executable { assertTrue(instanceInfo.kernelId == ec2Properties.instanceInfo["kernelId"]) },
                Executable { assertTrue(instanceInfo.pendingTime == ec2Properties.instanceInfo["pendingTime"]) },
                Executable { assertTrue(instanceInfo.privateIp == ec2Properties.instanceInfo["privateIp"]) },
                Executable { assertTrue(instanceInfo.ramdiskId == ec2Properties.instanceInfo["ramdiskId"]) },
                Executable { assertTrue(instanceInfo.region == ec2Properties.instanceInfo["region"]) },
                Executable { assertTrue(instanceInfo.version == ec2Properties.instanceInfo["version"]) }
        )
    }

    @Test
    fun testGetInstanceSignature() {
        val signature = testService.getInstanceSignature()
        assertTrue { signature == ec2Properties.instanceSignature }
    }

    @Test
    fun testGetInstanceType(){
        val type = testService.getInstanceType()
        assertTrue { type == ec2Properties.instanceType }
    }

    @Test
    fun testGetLocalHostName(){
        val type = testService.getLocalHostName()
        assertTrue { type == ec2Properties.localHostName }
    }

    @Test
    fun testGetMacAddress(){
        val type = testService.getMacAddress()
        assertTrue { type == ec2Properties.macAddress }
    }

    @Test
    fun testGetNetworkInterfaces(){
        val list = testService.getNetworkInterfaces()
        Assertions.assertAll(
                Executable{ assertTrue(list.isNotEmpty()) },
                Executable{ assertFalse(list[0].hostname.isNullOrBlank()) },
                Executable{ assertTrue(list[0].hostname == this.ec2Properties.networkInterface["hostname"])},
                Executable{ assertTrue(list[0].getIPv4Association("50.0.0.0").isNotEmpty())}
        )
    }

}

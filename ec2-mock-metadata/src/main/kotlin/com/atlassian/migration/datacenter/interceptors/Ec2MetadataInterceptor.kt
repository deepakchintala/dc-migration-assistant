package com.atlassian.migration.datacenter.interceptors

import com.amazonaws.util.EC2MetadataUtils
import com.amazonaws.util.EC2MetadataUtils.getMacAddress
import com.atlassian.migration.datacenter.properties.Ec2Properties
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Aspect
open class Ec2MetadataInterceptor() {

    var properties: Ec2Properties? = null

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getAmiId())")
    fun interceptGetAmiId(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.amiId
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getAmiLaunchIndex())")
    fun interceptGetAmiLaunchIndex(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.launchIndex
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getAmiManifestPath())")
    fun interceptGetAmiManifestPath(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.amiManifestPath
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getAncestorAmiIds())")
    fun interceptGetAncestorAmiIds(joinPoint: ProceedingJoinPoint?): List<String>? {
        return properties?.ancestorAmiIds
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getAvailabilityZone())")
    fun interceptGetAvailabilityZone(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.availabilityZone
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getBlockDeviceMapping())")
    fun interceptGetBlockDeviceMapping(joinPoint: ProceedingJoinPoint?): Map<String, String>? {
        return properties?.blockDeviceMapping
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getEC2InstanceRegion())")
    fun interceptGetEC2InstanceRegion(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.ec2InstanceRegion
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getHostAddressForEC2MetadataService())")
    fun interceptGetHostAddressForEC2MetadataService(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.hostAddressForEC2MetadataService
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getIAMInstanceProfileInfo())")
    fun interceptGetIAMInstanceProfileInfo(joinPoint: ProceedingJoinPoint?): EC2MetadataUtils.IAMInfo? {
        val info = EC2MetadataUtils.IAMInfo()
        val raw = properties?.iamInstanceProfileInfo
        info.code = raw?.get("code")
        info.instanceProfileArn = raw?.get("instanceProfileArn")
        info.instanceProfileId = raw?.get("instanceProfileId")
        info.lastUpdated = raw?.get("lastUpdated")
        info.message = raw?.get("message")
        return info
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getIAMSecurityCredentials())")
    fun interceptGetIAMSecurityCredentials(joinPoint: ProceedingJoinPoint?): Map<String, EC2MetadataUtils.IAMSecurityCredential>? {
        val credentials = EC2MetadataUtils.IAMSecurityCredential()
        val raw = properties?.iamSecurityCredential
        credentials.accessKeyId = raw?.get("accessKeyId")
        credentials.code = raw?.get("code")
        credentials.expiration = raw?.get("expiration")
        credentials.lastUpdated = raw?.get("lastUpdated")
        credentials.message = raw?.get("message")
        credentials.secretAccessKey = raw?.get("secretAccessKey")
        credentials.token = raw?.get("token")
        credentials.type = raw?.get("type")
        val valMap = HashMap<String, EC2MetadataUtils.IAMSecurityCredential>()
        valMap["test"] = credentials
        return valMap
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getInstanceAction())")
    fun interceptGetInstanceAction(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.instanceAction
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getInstanceId())")
    fun interceptGetInstanceId(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.instanceId
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getInstanceInfo())")
    fun interceptGetInstanceInfo(joinPoint: ProceedingJoinPoint?): EC2MetadataUtils.InstanceInfo? {
        val raw = properties?.instanceInfo

        val info = EC2MetadataUtils.InstanceInfo(raw?.get("pendingTime"),
                raw?.get("instanceType"),
                raw?.get("imageId"),
                raw?.get("instanceId"),
                raw?.get("billingProducts")!!.split(",").toTypedArray(),
                raw["architecture"],
                raw["accountId"],
                raw["kernelId"],
                raw["ramdiskId"],
                raw["region"],
                raw["version"],
                raw["availabilityZone"],
                raw["privateIp"],
                raw["devpayProductCodes"]!!.split(",").toTypedArray()
        )
        return info
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getInstanceSignature())")
    fun interceptGetInstanceSignature(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.instanceSignature
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getInstanceType())")
    fun interceptGetInstanceType(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.instanceType
    }

    @Around("execution(* com.amazonaws.util.EC2MetadataUtils.getLocalHostName())")
    fun interceptGetLocalHostName(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.localHostName
    }

    @Around("execution(static * com.amazonaws.util.EC2MetadataUtils.getMacAddress())")
    fun interceptGetMacAddress(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.macAddress
    }

    @Around("execution(static * com.amazonaws.util.EC2MetadataUtils.getNetworkInterfaces())")
    fun interceptGetNetworkInterfaces(joinPoint: ProceedingJoinPoint?): List<EC2MetadataUtils.NetworkInterface>? {
        return this.createNetworkInterfaces()
    }

    @Around("execution(public String com.amazonaws.util.EC2MetadataUtils.NetworkInterface.getHostname())")
    fun interceptNIGetItems(joinPoint: ProceedingJoinPoint?): String? {
        return properties?.networkInterface?.get("hostname")
    }

    private fun createNetworkInterfaces(): List<EC2MetadataUtils.NetworkInterface> {
        val r = Random()
        var count = r.nextInt((3))
        var i = 0;
        if (count == i) {
            count = 1
        }
        val list = ArrayList<EC2MetadataUtils.NetworkInterface>(count)
        while (i < count) {
            val item = EC2MetadataUtils.NetworkInterface(getMacAddress())
            list.add(item)
            i++
        }
        return list
    }

    public class NetworkInterface(macAddress: String?,  values:Map<String?,String?>?) : EC2MetadataUtils.NetworkInterface(macAddress) {

        override fun getHostname(): String? {
            return values
        }
    }


}
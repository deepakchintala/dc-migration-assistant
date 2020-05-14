package com.atlassian.migration.datacenter.services

import com.amazonaws.util.EC2MetadataUtils
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class TestService {

    fun getAmiId():String{
        return EC2MetadataUtils.getAmiId()
    }

    fun getAmiLaunchIndex():String{
        return EC2MetadataUtils.getAmiLaunchIndex();
    }

    fun getAmiManifestPath():String{
        return EC2MetadataUtils.getAmiManifestPath()
    }

    fun getAncestorAmiIds():List<String>{
        return EC2MetadataUtils.getAncestorAmiIds()
    }

    fun getAvailabilityZone():String{
        return EC2MetadataUtils.getAvailabilityZone()
    }

    fun getBlockDeviceMapping():Map<String,String>{
        return EC2MetadataUtils.getBlockDeviceMapping()
    }

    fun getEC2InstanceRegion():String{
        return EC2MetadataUtils.getEC2InstanceRegion()
    }

    fun getHostAddressForEC2MetadataService():String{
        return EC2MetadataUtils.getHostAddressForEC2MetadataService()
    }

    fun getIAMInstanceProfileInfo():EC2MetadataUtils.IAMInfo{
        return EC2MetadataUtils.getIAMInstanceProfileInfo()
    }

    fun getIAMSecurityCredentials(): Map<String,EC2MetadataUtils.IAMSecurityCredential>{
        return EC2MetadataUtils.getIAMSecurityCredentials()
    }

    fun getInstanceAction():String{
        return EC2MetadataUtils.getInstanceAction()
    }

    fun getInstanceId():String{
        return EC2MetadataUtils.getInstanceId()
    }

    fun getInstanceInfo():EC2MetadataUtils.InstanceInfo{
        return EC2MetadataUtils.getInstanceInfo()
    }

    fun getInstanceSignature():String{
        return EC2MetadataUtils.getInstanceSignature()
    }

    fun getInstanceType():String{
        return EC2MetadataUtils.getInstanceType()
    }

    fun getLocalHostName():String{
        return EC2MetadataUtils.getLocalHostName()
    }

    fun getMacAddress():String{
        return EC2MetadataUtils.getMacAddress()
    }

    fun getNetworkInterfaces():List<EC2MetadataUtils.NetworkInterface>{
        return EC2MetadataUtils.getNetworkInterfaces()
    }

}
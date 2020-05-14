package com.atlassian.migration.datacenter.properties

import com.amazonaws.util.EC2MetadataUtils
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix="ec2.metadata")
class Ec2Properties {

    lateinit var amiId:String
    lateinit var launchIndex:String
    lateinit var amiManifestPath:String
    lateinit var ancestorAmiIds:List<String>
    lateinit var availabilityZone:String
    lateinit var blockDeviceMapping:Map<String,String>
    lateinit var ec2InstanceRegion:String
    lateinit var hostAddressForEC2MetadataService:String
    lateinit var iamInstanceProfileInfo:Map<String,String>
    lateinit var iamSecurityCredential:Map<String,String>
    lateinit var instanceAction:String
    lateinit var instanceId:String
    lateinit var instanceInfo:Map<String,String>
    lateinit var instanceSignature:String
    lateinit var instanceType:String
    lateinit var localHostName:String
    lateinit var macAddress:String
    lateinit var networkInterface:Map<String,String>
}
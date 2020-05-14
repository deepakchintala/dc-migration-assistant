package com.atlassian.migration.datacenter.model

import com.amazonaws.util.EC2MetadataUtils

open class NetworkInterface(macAddress: String,  valueMap:Map<String,String>) : EC2MetadataUtils.NetworkInterface(macAddress) {

    override fun getHostname(): String? {
        return valueMap.get("hostname")
    }
}
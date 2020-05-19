package com.atlassian.migration.datacenter.spi.infrastructure

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ProvisioningConfigTest {
    @Test
    fun shouldGetListParametersAsCommaSeparatedValues() {
        val config = ProvisioningConfig("https://template.url", "stackName", object : HashMap<String, Any>() {
            init {
                put("azs", object : ArrayList<String?>() {
                    init {
                        add("us-east-2a")
                        add("us-east-2b")
                    }
                })
                put("password", "iamsupersecure.trustme")
                put("instanceCount", 2)
            }
        })
        val params = config.params
        Assertions.assertEquals("us-east-2a,us-east-2b", params["azs"])
        Assertions.assertEquals("iamsupersecure.trustme", params["password"])
        Assertions.assertEquals("2", params["instanceCount"])
    }
}
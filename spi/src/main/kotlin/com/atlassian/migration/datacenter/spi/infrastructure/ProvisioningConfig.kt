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
package com.atlassian.migration.datacenter.spi.infrastructure

class ProvisioningConfig {
    var templateUrl: String private set
    var stackName: String private set
    private var params: Map<String, Any>

    constructor(templateUrl: String, stackName: String, params: Map<String, Any>) {
        this.templateUrl = templateUrl
        this.stackName = stackName
        this.params = params
    }

    fun getParams(): Map<String, String> {
        return params.mapValues {
            if (it.value is List<*>) {
                // Type inference and smart-cast doesn't play well with List
                val list = it.value as List<*>
                list.joinToString(",")
            } else {
                it.value.toString()
            }
        }
    }
}
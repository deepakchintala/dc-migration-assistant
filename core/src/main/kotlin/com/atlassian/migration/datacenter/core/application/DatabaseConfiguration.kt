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
package com.atlassian.migration.datacenter.core.application

class DatabaseConfiguration(val type: DBType, val host: String, val port: Int, val name: String, val username: String, val password: String) {

    enum class DBType {
        POSTGRESQL,
        MYSQL,
        SQLSERVER,
        ORACLE,
        H2
    }

    companion object {
        fun h2(): DatabaseConfiguration {
            return DatabaseConfiguration(DBType.H2, "localhost", 0, "h2", "h2", "h2")
        }
    }

}
/*
 * Copyright (c) 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and  limitations under the License.
 */
package com.atlassian.migration.datacenter.core.application

import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration.DBType
import com.atlassian.migration.datacenter.spi.exceptions.ConfigurationReadException
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.net.URI
import java.util.stream.Stream

@JacksonXmlRootElement(localName = "jira-database-config")
class DatabaseConfigurationXmlElement {

    @JacksonXmlProperty(localName = "jdbc-datasource")
    private val dbConfigXmlElement: DbConfigXmlElement? = null

    fun toDatabaseConfiguration(): DatabaseConfiguration {
        if (dbConfigXmlElement == null) {
            throw ConfigurationReadException("No database configuration element found.")
        }
        val urlString = dbConfigXmlElement.url
        val userName = dbConfigXmlElement.userName
        val password = dbConfigXmlElement.password

        if (!validateRequiredValues(urlString, userName, password)) {
            throw ConfigurationReadException("Database configuration file has invalid values.")
        }

        val absURI = URI.create(urlString!!)
        val dbURI = URI.create(absURI.schemeSpecificPart)
        val type = DBType.valueOf(dbURI.scheme.toUpperCase())
        val host = dbURI.host
        var port = dbURI.port
        if (port == -1) port = 5432

        // TODO: handle connection param?;

        val name = dbURI.path.substring(1) // Remove leading '/'
        return DatabaseConfiguration(type, host, port, name, userName!!, password!!)
    }

    @Throws(ConfigurationReadException::class)
    private fun validateRequiredValues(vararg values: String?): Boolean {
        val containsInvalid = values.filter { it?.isBlank() ?: true }.any()
        return !containsInvalid
    }

    val isDataSourcePresent: Boolean
        get() = dbConfigXmlElement != null
}
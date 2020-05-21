package com.atlassian.migration.datacenter.util

import org.testcontainers.containers.PostgreSQLContainer

// Kotlin type-inference workaround: See https://github.com/testcontainers/testcontainers-java/issues/318
internal class PSQLContainerHelper(val image: String) : PostgreSQLContainer<PSQLContainerHelper>(image)

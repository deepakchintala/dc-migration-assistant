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
package com.atlassian.migration.datacenter.spi.fs.reporting

import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Tracks the progress of the file system migration
 */
@JsonSerialize(`as` = FileSystemMigrationProgress::class)
interface FileSystemMigrationProgress {
    /**
     * Gets the number of files which have been found to migrate.
     */
//    @get:JsonProperty("filesFound")
    fun getNumberOfFilesFound(): Long

    fun reportFileFound()

    /**
     * Gets the number of files which have had their upload commenced
     */
//    @get:JsonProperty("filesInFlight")
    fun getNumberOfCommencedFileUploads(): Long

    fun reportFileUploadCommenced()

    /**
     * Gets the number of files which have been successfully migrated
     */
//    @get:JsonProperty("migratedFiles")
    fun getCountOfMigratedFiles(): Long

    /**
     * Reports that a file was migrated successfully. Implementers should be careful that the underlying
     * collection is thread safe as this may be called from multiple file upload threads.
     */
    fun reportFileMigrated()
}
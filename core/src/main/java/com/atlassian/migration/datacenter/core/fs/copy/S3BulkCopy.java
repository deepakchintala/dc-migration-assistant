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

package com.atlassian.migration.datacenter.core.fs.copy;

import com.atlassian.migration.datacenter.core.aws.infrastructure.AWSMigrationHelperDeploymentService;
import com.atlassian.migration.datacenter.core.fs.Crawler;
import com.atlassian.migration.datacenter.core.fs.DirectoryStreamCrawler;
import com.atlassian.migration.datacenter.core.fs.FileSystemMigrationReportManager;
import com.atlassian.migration.datacenter.core.fs.FilesystemUploader;
import com.atlassian.migration.datacenter.core.fs.ReportType;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.core.fs.S3Uploader;
import com.atlassian.migration.datacenter.core.fs.Uploader;
import com.atlassian.migration.datacenter.core.fs.UploaderFactory;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.UPLOADING;

public class S3BulkCopy {

    private static final Logger logger = LoggerFactory.getLogger(S3BulkCopy.class);
    private static final String OVERRIDE_UPLOAD_DIRECTORY = System
            .getProperty("com.atlassian.migration.datacenter.fs.overrideJiraHome", "");

    private final Path home;
    private final FileSystemMigrationReportManager reportManager;
    private FilesystemUploader fsUploader;
    private UploaderFactory uploaderFactory;

    public S3BulkCopy(
        Path home,
        UploaderFactory uploaderFactory,
        FileSystemMigrationReportManager reportManager)
    {
        this.home = home;
        this.uploaderFactory = uploaderFactory;
        this.reportManager = reportManager;
    }

    public void copySharedHomeToS3() throws FilesystemUploader.FileUploadException {
        FileSystemMigrationReport report = reportManager.getCurrentReport(ReportType.Filesystem);

        if (report == null) {
            throw new FilesystemUploader.FileUploadException("No files system migration report bound to bulk copy operation");
        }
        report.setStatus(UPLOADING);

        Uploader s3Uploader = uploaderFactory.newUploader(report);

        // TODO: These should probably be factories too
        Crawler homeCrawler = new DirectoryStreamCrawler(report);
        fsUploader = new FilesystemUploader(homeCrawler, s3Uploader);

        logger.info("commencing upload of shared home");

        fsUploader.uploadDirectory(getSharedHomeDir());

        logger.info("upload of shared home complete.");
    }

    public void abortCopy() {
        if (fsUploader == null) {
            return;
        }

        logger.warn("Aborting running filesystem migration");
        fsUploader.abort();
    }


    private Path getSharedHomeDir() {
        if (!OVERRIDE_UPLOAD_DIRECTORY.equals("")) {
            return Paths.get(OVERRIDE_UPLOAD_DIRECTORY);
        }
        return home;
    }
}

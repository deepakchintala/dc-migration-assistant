package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationStatus;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3FilesystemMigrationServiceTest {
    @Test
    public void migrationShouldFailWithIncorrectDirectory() {
        // given
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        FilesystemMigrationConfig config = new FilesystemMigrationConfig("s3bucket", nonexistentDir);
        S3FilesystemMigrationService fsService = new S3FilesystemMigrationService();
        // when
        FilesystemMigrationProgress progress = fsService.startMigration(config);
        // then
        assertEquals(FilesystemMigrationStatus.FAILED, progress.getStatus());
    }
}

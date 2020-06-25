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

package com.atlassian.migration.datacenter.captor;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.migration.datacenter.core.fs.captor.DefaultAttachmentCaptor;
import com.atlassian.migration.datacenter.dto.FileSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(ActiveObjectsJUnitRunner.class)
public class DefaultAttachmentCaptorTest {

    private ActiveObjects ao;
    private EntityManager entityManager;

    private DefaultAttachmentCaptor sut;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    MigrationService migrationService;

    @Before
    public void setup() {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        sut = new DefaultAttachmentCaptor(ao, migrationService);
        setupEntities();
    }

    private void setupEntities() {
        ao.migrate(Migration.class);
        ao.migrate(FileSyncRecord.class);
    }


    @Test
    public void shouldStoreAttachmentPathInDatabase() throws IOException {
        File oneAttachmentFile = File.createTempFile("one-attachment-file", "");
        oneAttachmentFile.deleteOnExit();

        sut.captureAttachment(oneAttachmentFile, null);

        FileSyncRecord record = ao.find(FileSyncRecord.class)[0];

        assertEquals(oneAttachmentFile.getPath(), record.getFilePath());
    }

    @Test
    public void shouldCaptureAttachmentAndThumbnailPathInDatabase() throws IOException {
        File oneAttachmentFile = File.createTempFile("one-attachment-file", "");
        File oneThumbnailFile = File.createTempFile("one-thumbnail-file", "");

        sut.captureAttachment(oneAttachmentFile, oneThumbnailFile);

        FileSyncRecord[] fileSyncRecords = ao.find(FileSyncRecord.class);
        assertEquals(2, fileSyncRecords.length);

        List<String> actualFilePaths = Arrays.stream(fileSyncRecords).map(FileSyncRecord::getFilePath).collect(Collectors.toList());
        assertThat(actualFilePaths, IsIterableContainingInOrder.contains(oneAttachmentFile.getPath(), oneThumbnailFile.getPath()));
    }

    @Test
    public void shouldCaptureJustAttachmentPathWhenThumbnailDoesNotPhysicallyExist() throws IOException {
        File oneAttachmentFile = File.createTempFile("oneAttachmentFile", "");
        oneAttachmentFile.deleteOnExit();

        File tempThumbnailFile = File.createTempFile("soon-tobe-deleted", "");
        tempThumbnailFile.delete();

        sut.captureAttachment(oneAttachmentFile, tempThumbnailFile);

        FileSyncRecord[] fileSyncRecords = ao.find(FileSyncRecord.class);
        assertEquals(1, fileSyncRecords.length);

        assertThat(fileSyncRecords[0].getFilePath(), Matchers.is(oneAttachmentFile.getPath()));
    }

    @Test
    public void shouldCaptureJustThumbnailPathWhenAttachmentFileDoesNotPhysicallyExist() throws IOException {
        File oneAttachmentFile = File.createTempFile("oneAttachmentFile", "");
        oneAttachmentFile.delete();

        File oneThumbnailFile = File.createTempFile("one-thumbnail-file", "");
        oneThumbnailFile.deleteOnExit();

        sut.captureAttachment(oneAttachmentFile, oneThumbnailFile);

        FileSyncRecord[] fileSyncRecords = ao.find(FileSyncRecord.class);
        assertEquals(1, fileSyncRecords.length);

        assertThat(fileSyncRecords[0].getFilePath(), Matchers.is(oneThumbnailFile.getPath()));
    }

    @Test
    public void shouldNotCaptureAnyFilesWhenTheyDoNotPhysicallyExist() throws IOException {
        File oneAttachmentFile = File.createTempFile("oneAttachmentFile", "");
        oneAttachmentFile.delete();

        File oneThumbnailFile = File.createTempFile("one-thumbnail-file", "");
        oneThumbnailFile.delete();

        sut.captureAttachment(oneAttachmentFile, oneThumbnailFile);

        FileSyncRecord[] fileSyncRecords = ao.find(FileSyncRecord.class);
        assertEquals(0, fileSyncRecords.length);
    }

    @Test
    public void shouldStoreRecordAgainstCurrentMigration() throws IOException {
        File oneAttachmentFile = File.createTempFile("one-attachment-file", "");
        Migration migration = givenMigrationExists();

        sut.captureAttachment(oneAttachmentFile, null);
        FileSyncRecord record = ao.find(FileSyncRecord.class)[0];
        assertEquals(migration, record.getMigration());
    }
    
    @NotNull
    private Migration givenMigrationExists() {
        Migration migration = ao.create(Migration.class);
        migration.save();

        when(migrationService.getCurrentMigration()).thenReturn(migration);
        return migration;
    }


}
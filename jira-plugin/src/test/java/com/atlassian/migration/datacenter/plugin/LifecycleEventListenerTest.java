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

package com.atlassian.migration.datacenter.plugin;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LifecycleEventListenerTest {

    private LifecycleEventListener eventListener;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private DatabaseMigrationService databaseMigrationService;

    @Mock
    private FilesystemMigrationService filesystemMigrationService;

    @Mock
    private S3FinalSyncService s3FinalSyncService;

    @Mock
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        eventListener = new LifecycleEventListener(eventPublisher, databaseMigrationService, filesystemMigrationService, s3FinalSyncService);
    }

    @Test
    void shouldUnscheduleJobsOnPluginDisable() {
        when(plugin.getKey()).thenReturn("com.atlassian.migration.datacenter.jira-plugin");
        eventListener.onPluginDisabled(new PluginDisabledEvent(plugin));

        verify(databaseMigrationService).unscheduleMigration();
        verify(filesystemMigrationService).unscheduleMigration();
        verify(s3FinalSyncService).unscheduleMigration();
    }
}
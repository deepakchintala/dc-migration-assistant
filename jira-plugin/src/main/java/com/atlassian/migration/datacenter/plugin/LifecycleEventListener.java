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

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.datacenter.core.aws.db.DatabaseMigrationService;
import com.atlassian.migration.datacenter.core.fs.captor.S3FinalSyncService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class LifecycleEventListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LifecycleEventListener.class);
    private static final String PLUGIN_KEY = "com.atlassian.migration.datacenter.jira-plugin";
    private final EventPublisher eventPublisher;
    private final DatabaseMigrationService databaseMigrationService;
    private final FilesystemMigrationService filesystemMigrationService;
    private final S3FinalSyncService defaultS3FinalSyncService;

    public LifecycleEventListener(EventPublisher eventPublisher, DatabaseMigrationService databaseMigrationService, FilesystemMigrationService filesystemMigrationService, S3FinalSyncService defaultS3FinalSyncService) {
        this.eventPublisher = eventPublisher;
        this.databaseMigrationService = databaseMigrationService;
        this.filesystemMigrationService = filesystemMigrationService;
        this.defaultS3FinalSyncService = defaultS3FinalSyncService;
    }

    @Override
    public void destroy() throws Exception {
        this.eventPublisher.unregister(this);
        unregisterJobs();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register(this);
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        if (PLUGIN_KEY.equals(event.getPlugin().getKey())) {
            unregisterJobs();
        }
    }

    private void unregisterJobs() {
        try{
            this.databaseMigrationService.unscheduleMigration();
            this.filesystemMigrationService.unscheduleMigration();
            this.defaultS3FinalSyncService.unscheduleMigration();
        } catch (Exception ex) {
            log.info("error while unregistering all scheduled jobs. Message {}", ex.getMessage());
        }
    }
}

package com.atlassian.migration.datacenter.jira.fs.captor;


import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.dto.AttachmentSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.fs.captor.AttachmentSyncService;

public class AttachmentSyncServiceImpl implements AttachmentSyncService {

    private MigrationService migrationService;
    private ActiveObjects activeObjects;

    public AttachmentSyncServiceImpl(MigrationService migrationService, ActiveObjects activeObjects) {
        this.migrationService = migrationService;
        this.activeObjects = activeObjects;
    }

    @Override
    public boolean recordAttachment(Long attachmentId, Long entityId) {
        Migration currentMigration = this.migrationService.getCurrentMigration();
        //Todo: Handle existing?
        AttachmentSyncRecord syncRecord = this.activeObjects.create(AttachmentSyncRecord.class);

        syncRecord.setAttachmentId(attachmentId);
        syncRecord.setHolderEntityId(entityId);
        syncRecord.setMigration(currentMigration);

        syncRecord.save();
        return true;
    }
}

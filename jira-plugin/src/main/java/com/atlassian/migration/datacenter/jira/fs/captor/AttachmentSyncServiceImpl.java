package com.atlassian.migration.datacenter.jira.fs.captor;


import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.migration.datacenter.dto.AttachmentSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.fs.captor.AttachmentSyncService;
import net.java.ao.Query;

import java.io.IOException;
import java.io.InputStream;

public class AttachmentSyncServiceImpl implements AttachmentSyncService {

    private MigrationService migrationService;
    private ActiveObjects activeObjects;
    private final AttachmentManager attachmentManager;

    public AttachmentSyncServiceImpl(MigrationService migrationService, ActiveObjects activeObjects, AttachmentManager attachmentManager) {
        this.migrationService = migrationService;
        this.activeObjects = activeObjects;
        this.attachmentManager = attachmentManager;
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

    @Override
    public void processAttachments() {
        //TODO: Obviously, this is stub code. This will be replaced by a query to find unprocessed results. Pagination may be required etc..
        AttachmentSyncRecord[] attachmentSyncRecords = this.activeObjects.find(AttachmentSyncRecord.class);
        //Query.select().where("isProcessed is ?", Boolean.FALSE));
        for (AttachmentSyncRecord record: attachmentSyncRecords) {
            Attachment attachment = loadAttachment(record);
            try {
                this.attachmentManager.streamAttachmentContent(attachment, this::uploadToS3);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean uploadToS3(InputStream is) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private Attachment loadAttachment(AttachmentSyncRecord record) {
        //Load attachment by record Id
        return null;
    }
}

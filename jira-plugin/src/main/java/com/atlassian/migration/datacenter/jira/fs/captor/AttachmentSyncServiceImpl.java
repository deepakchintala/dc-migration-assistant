package com.atlassian.migration.datacenter.jira.fs.captor;


import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.migration.datacenter.core.fs.S3MultiPartUploader;
import com.atlassian.migration.datacenter.core.fs.S3UploadConfig;
import com.atlassian.migration.datacenter.dto.AttachmentSyncRecord;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.fs.captor.AttachmentSyncService;
import net.java.ao.Query;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

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
        //TODO: S3uploadConfig contains the root path, s3Client and the bucketName
        S3UploadConfig config = null;
        //TODO: We need to form the key to pass in - the path is used to form the S3 object key
        //This bucket key is relative to jira home - i.e. data/attachments/issue-key/...
        String key = "";
        try {
            new S3MultiPartUploader(config, null, key).upload(is);
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
        return true;
    }

    private Attachment loadAttachment(AttachmentSyncRecord record) {
        //Load attachment by record Id
        return null;
    }
}

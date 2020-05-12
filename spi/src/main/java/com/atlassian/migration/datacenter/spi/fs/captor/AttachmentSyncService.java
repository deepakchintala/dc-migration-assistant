package com.atlassian.migration.datacenter.spi.fs.captor;

public interface AttachmentSyncService {
    /**
     * Store a record of an attachment that has been modified while a migration is in progress
     *
     * @param attachmentId, a <code>Long</code> identifier of the attachment
     * @param entityId <code>Long</code> identifier of the entity that owns the attachment
     * @return a <code>Boolean</code> value that denotes if the record has been persisted
     */
    boolean recordAttachment(Long attachmentId, Long entityId);

    void processAttachments();
}

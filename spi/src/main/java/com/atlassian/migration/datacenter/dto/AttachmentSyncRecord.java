package com.atlassian.migration.datacenter.dto;

import net.java.ao.Entity;

public interface AttachmentSyncRecord extends Entity {
    Migration getMigration();
    void setMigration(Migration migration);

    Long getAttachmentId();
    void setAttachmentId(Long id);

    Long getHolderEntityId();
    void setHolderEntityId(Long id);

    boolean getIsProcessed();
    boolean setIsProcessed(boolean isProcessed);
}

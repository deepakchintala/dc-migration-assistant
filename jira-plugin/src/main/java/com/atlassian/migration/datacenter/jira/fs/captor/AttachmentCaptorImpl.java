package com.atlassian.migration.datacenter.jira.fs.captor;

import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.migration.datacenter.spi.fs.captor.AttachmentSyncService;


public class AttachmentCaptorImpl implements AttachmentCaptor {

    private final AttachmentSyncService attachmentSyncService;

    public AttachmentCaptorImpl(AttachmentSyncService attachmentSyncService) {
        this.attachmentSyncService = attachmentSyncService;
    }

    @Override
    public void recordAttachment(Attachment attachment) {
        this.attachmentSyncService.recordAttachment(attachment.getId(), attachment.getIssueId());
    }
}

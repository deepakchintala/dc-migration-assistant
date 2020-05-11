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

package com.atlassian.migration.datacenter.core.fs.capture;


import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.nio.file.Paths;

public class JiraIssueAttachmentListener implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JiraIssueAttachmentListener.class);

    private final AttachmentCapturer attachmentCapturer;
    private final AttachmentStore attachmentStore;
    private final EventPublisher eventPublisher;

    public JiraIssueAttachmentListener(EventPublisher eventPublisher,
                                       AttachmentCapturer attachmentCapturer,
                                       AttachmentStore attachmentStore) {
        this.eventPublisher = eventPublisher;
        this.attachmentCapturer = attachmentCapturer;
        this.attachmentStore = attachmentStore;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("registering with event publisher");
        eventPublisher.register(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        logger.trace("received jira event with type {}", issueEvent.getEventTypeId());
        if (issueEvent.getEventTypeId().equals(EventType.ISSUE_CREATED_ID)) {
            logger.trace("got issue created event");

            issueEvent.getIssue().getAttachments().forEach(
                    attachment -> {
                        File file = attachmentStore.getAttachmentFile(attachment);
                        logger.trace("found file {} on issue event (deprecated api)", file.getAbsolutePath());
                        File thumb = attachmentStore.getThumbnailFile(attachment);
                        logger.trace("found thumbnail {} on issue event (deprecated api)", thumb.getAbsolutePath());
//                        attachmentCapturer.captureAttachment(Paths.get(attachment.getFilename()));
                    }
            );
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Destroying migration assistant plugin. De-registering with event publisher");
        eventPublisher.unregister(this);
    }
}

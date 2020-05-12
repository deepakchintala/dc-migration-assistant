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

package com.atlassian.migration.datacenter.jira.fs.captor;


import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.AttachmentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class JiraIssueAttachmentManagerListener implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JiraIssueAttachmentManagerListener.class);

    private final AttachmentCaptor attachmentCaptor;
    private final EventPublisher eventPublisher;
    private final AttachmentManager attachmentManager;

    public JiraIssueAttachmentManagerListener(EventPublisher eventPublisher, AttachmentCaptor attachmentCaptor, AttachmentManager attachmentManager) {
        this.eventPublisher = eventPublisher;
        this.attachmentCaptor = attachmentCaptor;
        this.attachmentManager = attachmentManager;
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
            issueEvent
                    .getIssue()
                    .getAttachments()
                    .forEach(attachmentCaptor::recordAttachment);
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Destroying migration assistant plugin. De-registering with event publisher");
        eventPublisher.unregister(this);
    }
}

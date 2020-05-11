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

public class JiraIssueAttachmentListener {

    AttachmentCapturer attachmentCapturer;

    public JiraIssueAttachmentListener(EventPublisher eventPublisher, AttachmentCapturer attachmentCapturer) {
        eventPublisher.register(this);
        this.attachmentCapturer = attachmentCapturer;
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {

    }

}

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

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraIssueAttachmentLocationCaptorTest {

    public static final String A_MOCK_ATTACHMENT_PATH = "a/mock/attachment";
    public static final String ANOTHER_MOCK_ATTACHMENT_PATH = "another/mock/attachment";

    @Mock
    EventPublisher mockPublisher;

    @InjectMocks
    JiraIssueAttachmentListener sut;

    @Mock
    Issue mockIssue;

    @Mock
    Attachment aMockAttachment;

    @Mock
    Attachment anotherMockAttachment;

    private List<Path> capturedPaths = new LinkedList<>();

    @BeforeEach
    void setUp() {
        sut = new JiraIssueAttachmentListener(mockPublisher, path -> capturedPaths.add(path));
    }

    @Test
    void shouldCaptureAttachmentInIssueCreatedEvent() {
        when(aMockAttachment.getFilename()).thenReturn(A_MOCK_ATTACHMENT_PATH);
        when(anotherMockAttachment.getFilename()).thenReturn(ANOTHER_MOCK_ATTACHMENT_PATH);
        when(mockIssue.getAttachments()).thenReturn(new ArrayList<Attachment>() {{
            add(aMockAttachment);
            add(anotherMockAttachment);
        }});
        IssueEvent mockEvent = new IssueEvent(mockIssue, null, null, null, null, null, EventType.ISSUE_CREATED_ID);
        sut.onIssueEvent(mockEvent);

        assertThat(capturedPaths, contains(
                Paths.get(A_MOCK_ATTACHMENT_PATH),
                Paths.get(ANOTHER_MOCK_ATTACHMENT_PATH)
        ));
    }
}
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

package com.atlassian.migration.datacenter.core.fs.captor;

import com.atlassian.activeobjects.external.ActiveObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class DefaultAttachmentPathCaptor implements AttachmentPathCaptor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAttachmentPathCaptor.class);
    private final ActiveObjects ao;

    public DefaultAttachmentPathCaptor(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public void captureAttachmentPath(Path attachmentPath) {
        logger.debug("captured attachment for final sync: {}", attachmentPath.toString());

        FileSyncRecord record = ao.create(FileSyncRecord.class);

        record.setFilePath(attachmentPath.toString());

        record.save();
    }
}

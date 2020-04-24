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

import { MigrationDuration } from './common';

const dbAPIBase = 'migration/db';
export const dbStatusReportEndpoint = `${dbAPIBase}/report`;

export enum DBMigrationStatus {
    NOT_STARTED,
    EXPORTING,
    UPLOADING,
    IMPORTING,
    DONE,
    FAILED,
}

export const toI18nProp = (status: DBMigrationStatus): string => {
    const name = status.toString().toLowerCase();
    return `atlassian.migration.datacenter.db.status.${name}`;
};

// See DatabaseMigrationProgress.kt
export type DatabaseMigrationStatus = {
    status: DBMigrationStatus;
    elapsedTime: MigrationDuration;
};

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

import React, { FunctionComponent } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';

import { MigrationTransferPage } from '../../shared/MigrationTransferPage';
import { ProgressBuilder, Progress } from '../../shared/Progress';

export const ProvisioningStatusPage: FunctionComponent = () => {
    return (
        <MigrationTransferPage
            description={I18n.getText('atlassian.migration.datacenter.provision.aws.description')}
            getProgress={(): Promise<Progress> =>
                Promise.resolve(new ProgressBuilder().setPhase('').build())
            }
            hasStarted
            heading={I18n.getText('atlassian.migration.datacenter.provision.aws.title')}
            nextText={I18n.getText('atlassian.migration.datacenter.generic.next')}
            // This page is only rendered when provisioning has already started. The deployment will be started by the QuickstartDeploy page
            startMigrationPhase={Promise.resolve}
        />
    );
};

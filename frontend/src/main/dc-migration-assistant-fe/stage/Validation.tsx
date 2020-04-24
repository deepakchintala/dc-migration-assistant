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

import React, { FunctionComponent, ReactFragment, useEffect, useState } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';
import SectionMessage from '@atlaskit/section-message';
import TableTree, { Cell, Row } from '@atlaskit/table-tree';
import { Button } from '@atlaskit/button/dist/esm/components/Button';
import { callAppRest, RestApiPathConstants } from '../utils/api';

const renderMigrationSummaryActionCallout = (): ReactFragment => {
    return (
        <>
            <div>
                <h4>Actions required on your side</h4>
            </div>
            <div>
                <ul>
                    <li>
                        {I18n.getText('atlassian.migration.datacenter.validation.post.action.1')}
                    </li>
                    <li>
                        {I18n.getText('atlassian.migration.datacenter.validation.post.action.2')}
                    </li>
                    <li>
                        {I18n.getText('atlassian.migration.datacenter.validation.post.action.3')}
                    </li>
                    <li>
                        {I18n.getText('atlassian.migration.datacenter.validation.post.action.4')}
                    </li>
                </ul>
            </div>
        </>
    );
};

type MigrationSummaryData = {
    key: string;
    value: string;
};

// Use a simple div with a display:table instead
const MigrationSummary: FunctionComponent = () => {
    const [summaryData, setSummaryData]: [Array<MigrationSummaryData>, Function] = useState<
        Array<MigrationSummaryData>
    >([]);

    useEffect(() => {
        callAppRest('GET', RestApiPathConstants.getMigrationContextPath)
            .then(response => response.json())
            .then(data => {
                setSummaryData(
                    Object.entries(data).map(summary => {
                        const [key, value] = summary;
                        return { key, value };
                    })
                );
            })
            .catch(() => {
                Promise.resolve([
                    { key: 'instanceUrl', value: 'http://loadbalancer' },
                    {
                        key: 'migrationDuration',
                        value: '1/Apr/2020 08:00 AM - 2/Apr/2020 12:00 PM',
                    },
                    { key: 'fileTransferCount', value: '12345 of 12345' },
                    { key: 'databaseSize', value: '34 GB of 34 GB' },
                ]).then(data => setSummaryData(data));
            });
    }, []);

    return (
        <>
            <div>
                <h4>Migration details</h4>
            </div>
            <TableTree>
                {summaryData.map(summary => {
                    const phraseId = `atlassian.migration.datacenter.validation.summary.phrase.${summary.key}`;
                    return (
                        <Row key={`migration-summary-${summary.key}`} hasChildren={false}>
                            <Cell width={400} singleLine>
                                {I18n.getText(phraseId)}
                            </Cell>
                            <Cell width={400}>{summary.value}</Cell>
                        </Row>
                    );
                })}
            </TableTree>
        </>
    );
};

export const ValidateStagePage: FunctionComponent = () => {
    return (
        <>
            <h3>{I18n.getText('atlassian.migration.datacenter.step.validation.phrase')}</h3>
            <h3>{I18n.getText('atlassian.migration.datacenter.validation.message')}</h3>
            <SectionMessage appearance="info">
                {I18n.getText('atlassian.migration.datacenter.validation.section.message')}
            </SectionMessage>
            <MigrationSummary />
            {renderMigrationSummaryActionCallout()}
            <Button isLoading={false} appearance="primary">
                {I18n.getText('atlassian.migration.datacenter.validation.next.button')}
            </Button>
        </>
    );
};

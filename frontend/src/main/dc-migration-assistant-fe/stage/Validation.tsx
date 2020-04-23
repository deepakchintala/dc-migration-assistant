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

type MigrationSummaryData = {
    key: string;
    value: string;
};

const renderMigrationSummaryActionCallout = (): ReactFragment => {
    return (
        <>
            <div>
                <h4>Actions required on your side</h4>
            </div>
            <div>
                <ul>{I18n.getText('atlassian.migration.datacenter.validation.post.action.1')}</ul>
                <ul>{I18n.getText('atlassian.migration.datacenter.validation.post.action.2')}</ul>
                <ul>{I18n.getText('atlassian.migration.datacenter.validation.post.action.3')}</ul>
                <ul>{I18n.getText('atlassian.migration.datacenter.validation.post.action.4')}</ul>
            </div>
        </>
    );
};

// Use a simple div with a display:table instead
const MigrationSummary: FunctionComponent = () => {
    const [summaryData, setSummaryData]: [Array<MigrationSummaryData>, Function] = useState<
        Array<MigrationSummaryData>
    >([]);

    useEffect(() => {
        Promise.resolve([
            { key: 'AWS Jira Instance', value: 'http://loadbalancer' },
            { key: 'Migration Time', value: '1/Apr/2020 08:00 AM - 2/Apr/2020 12:00 PM'},
            { key: 'Total number of files transferred', value: '12345 of 12345'},
            { key: 'Database size transferred', value: '34 GB of 34 GB'},
        ]).then(data => setSummaryData(data));
    }, []);

    return (
        <>
            <div>
                <h4>Migration details</h4>
            </div>
            <TableTree>
                {summaryData.map(summary => (
                    <Row
                        key={`migration-summary-${summary.key
                            .toLocaleLowerCase()
                            .replace(' ', '_')}`}
                        hasChildren={false}
                    >
                        <Cell width={400} singleLine>
                            {summary.key}
                        </Cell>
                        <Cell width={400}>{summary.value}</Cell>
                    </Row>
                ))}
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

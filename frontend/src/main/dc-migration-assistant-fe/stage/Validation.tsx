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

const INSTANCE_URL_MIGRATION_CONTEXT_KEY = 'instanceUrl';

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
                setSummaryData([]);
            });
    }, []);

    return (
        <>
            <div>
                <h4>Migration details</h4>
            </div>
            <TableTree>
                {summaryData
                    .filter(x => x.key === INSTANCE_URL_MIGRATION_CONTEXT_KEY)
                    .map(summary => {
                        return (
                            <Row key={`migration-summary-${summary.key}`} hasChildren={false}>
                                <Cell width={400} singleLine>
                                    {I18n.getText(
                                        'atlassian.migration.datacenter.validation.summary.phrase.instanceUrl'
                                    )}
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

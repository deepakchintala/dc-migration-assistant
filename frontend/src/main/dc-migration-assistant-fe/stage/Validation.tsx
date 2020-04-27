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

import React, { FunctionComponent, ReactElement, useEffect, useState } from 'react';
import { I18n } from '@atlassian/wrm-react-i18n';
import SectionMessage from '@atlaskit/section-message';
import TableTree, { Cell, Row } from '@atlaskit/table-tree';
import { Button } from '@atlaskit/button/dist/esm/components/Button';
import styled from 'styled-components';
import { useHistory } from 'react-router-dom';
import { callAppRest, RestApiPathConstants } from '../utils/api';
import { homePath } from '../utils/RoutePaths';

const MigrationSummaryContainer = styled.div`
    display: grid;
    justify-items: start;
    margin-top: 25px;
    & li {
        align-self: center;
    }
    & div {
        padding-bottom: 5px;
    }
`;

const MigrationDetailsContainer = styled.div`
    margin-top: 25px;
`;

const MigrationSummaryActionCallout = (): ReactElement => {
    return (
        <MigrationSummaryContainer>
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
        </MigrationSummaryContainer>
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
        <MigrationDetailsContainer>
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
        </MigrationDetailsContainer>
    );
};

export const ValidateStagePage: FunctionComponent = () => {
    const history = useHistory();
    const defaultButtonStyle = {
        marginTop: '15px',
    };
    const redirectUserToHome = (): void => {
        history.push(homePath);
    };

    return (
        <>
            <h3>{I18n.getText('atlassian.migration.datacenter.step.validation.phrase')}</h3>
            <h3>{I18n.getText('atlassian.migration.datacenter.validation.message')}</h3>
            <SectionMessage appearance="info">
                {I18n.getText('atlassian.migration.datacenter.validation.section.message')}
            </SectionMessage>
            <MigrationSummary />
            <MigrationSummaryActionCallout />
            <Button
                isLoading={false}
                appearance="primary"
                style={defaultButtonStyle}
                onClick={redirectUserToHome}
            >
                {I18n.getText('atlassian.migration.datacenter.validation.next.button')}
            </Button>
        </>
    );
};

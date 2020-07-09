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
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';
import SectionMessage from '@atlaskit/section-message';

import { Link, Redirect } from 'react-router-dom';
import Button from '@atlaskit/button';
import Spinner from '@atlaskit/spinner';
import { migration, MigrationStage } from '../api/migration';
import { homePath } from '../utils/RoutePaths';
import { getPathForStage } from '../utils/migration-stage-to-path';
import { useCurrentStageRedirect } from '../hooks/useCurrentStageRedirect';

const MigrationErrorContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

type ResetMigrationProps = {
    resetMigrationFunc: VoidFunction;
};

const buttonStyle = {
    marginTop: '20px',
};

const MigrationError = ({ resetMigrationFunc }: ResetMigrationProps): ReactElement => {
    const [errorReason, setErrorReason] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(true);

    useCurrentStageRedirect();

    useEffect(() => {
        setLoading(true);
        migration
            .getMigrationSummary()
            .then(summary => {
                setErrorReason(summary.error);
            })
            .finally(() => setLoading(false));
    }, []);

    return (
        <>
            <h2>{I18n.getText('atlassian.migration.datacenter.generic.error')}</h2>
            <SectionMessage appearance="error">
                <p>
                    {I18n.getText('atlassian.migration.datacenter.error.section.message')}{' '}
                    <a
                        target="_blank"
                        rel="noreferrer noopener"
                        href="https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html?#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-errors"
                    >
                        {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                    </a>
                </p>
            </SectionMessage>
            <p>
                {I18n.getText('atlassian.migration.datacenter.error.reason')}
                {': '}
                {loading ? (
                    <Spinner />
                ) : (
                    errorReason ||
                    I18n.getText('atlassian.migration.datacenter.error.reason.unknown')
                )}
            </p>
            <Button onClick={resetMigrationFunc} appearance="primary" style={buttonStyle}>
                {I18n.getText('atlassian.migration.datacenter.error.reset.button')}
            </Button>
        </>
    );
};

const NoMigrationError: FunctionComponent<{ currentStage: MigrationStage }> = ({
    currentStage,
}) => {
    return (
        <>
            <h2>{I18n.getText('atlassian.migration.datacenter.generic.migration.in_progress')}</h2>
            <SectionMessage appearance="warning">
                <p>
                    {I18n.getText('atlassian.migration.datacenter.error.section.warning.message')}
                </p>
            </SectionMessage>
            <Link to={getPathForStage(currentStage)}>
                <Button appearance="primary" style={buttonStyle}>
                    {I18n.getText('atlassian.migration.datacenter.error.view.migration.button')}
                </Button>
            </Link>
        </>
    );
};

export const MigrationErrorPage: FunctionComponent = () => {
    const [currentStage, setCurrentStage] = useState<MigrationStage>(MigrationStage.NOT_STARTED);
    const [redirectToNewMigration, setRedirectToNewMigration] = useState<boolean>(false);
    const [loadingCurrentStage, setLoadingCurrentStage] = useState<boolean>(true);

    useEffect(() => {
        migration
            .getMigrationStage()
            .then(stage => {
                setCurrentStage(stage);
            })
            .finally(() => {
                setLoadingCurrentStage(false);
            });
    }, []);

    // TODO: We may need to handle error from this API in the future
    const resetMigration = (): void => {
        migration.resetMigration().then(() => {
            setRedirectToNewMigration(true);
        });
    };

    if (redirectToNewMigration) {
        return <Redirect to={homePath} push />;
    }

    if (loadingCurrentStage) {
        return <Spinner />;
    }

    if (currentStage === MigrationStage.ERROR) {
        return (
            <MigrationErrorContainer>
                <MigrationError resetMigrationFunc={resetMigration} />
            </MigrationErrorContainer>
        );
    }

    return <NoMigrationError currentStage={currentStage} />;
};

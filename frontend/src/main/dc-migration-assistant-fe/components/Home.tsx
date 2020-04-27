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

import React, { ReactElement, useState } from 'react';
import Button from '@atlaskit/button';
import InlineMessage from '@atlaskit/inline-message';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';

import { startPath } from '../utils/RoutePaths';
import { migration } from '../api/migration';
import { ErrorFlag } from './shared/ErrorFlag';

type HomeProps = {
    title: string;
    synopsis: string;
    startButtonText: string;
};

const HomeContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

const ButtonContainer = styled.div`
    margin-top: 250px;
    align-self: flex-end;
`;

const InfoProps = {
    title: I18n.getText('atlassian.migration.datacenter.home.info.title'),
    secondaryText: I18n.getText('atlassian.migration.datacenter.home.info.content'),
};

export const Home = ({ title, synopsis, startButtonText }: HomeProps): ReactElement => {
    const [error, setError] = useState<string>();
    const [loading, setLoading] = useState<boolean>(false);

    const createMigration = (): void => {
        setLoading(true);
        migration
            .createMigration()
            .then(() => {
                // Route to next page
            })
            .catch(err => {
                setError(err);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    return (
        <HomeContainer>
            <h2>{title}</h2>
            <p>{synopsis}</p>
            <ErrorFlag
                showError={error && error !== ''}
                dismissErrorFunc={(): void => setError('')}
                // FIXME: Internationalisation
                title="Unable to create migration"
                description={error}
                id="migration-creation-error"
            />
            <InlineMessage {...InfoProps} />
            <ButtonContainer>
                <Button isLoading={loading} appearance="primary" onClick={createMigration}>
                    {startButtonText}
                </Button>
            </ButtonContainer>
        </HomeContainer>
    );
};

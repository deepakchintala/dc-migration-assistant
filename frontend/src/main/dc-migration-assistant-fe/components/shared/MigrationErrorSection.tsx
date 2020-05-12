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
import SectionMessage from '@atlaskit/section-message';
import styled from 'styled-components';

import { CommandDetails as CommandResult } from '../../api/db';

const ErrorFragment = styled.div`
    margin-top: 20px;
`;

type CommandResultProps = {
    result: CommandResult;
};

export const MigrationErrorSection: FunctionComponent<CommandResultProps> = ({
    result: commandResult,
}) => {
    return (
        <ErrorFragment>
            <SectionMessage appearance="warning" title="Database sync warning">
                <p>
                    We encountered some errors during the database sync. Some of these errors
                    aren&apos;t necessarily fatal, and you can continue with the migration if you
                    want. Before doing so, we recommend you review the errors first.
                </p>

                <p>
                    <a href={commandResult.consoleUrl} target="_blank" rel="noopener noreferrer">
                        View the errors in S3
                    </a>
                </p>
            </SectionMessage>
        </ErrorFragment>
    );
};

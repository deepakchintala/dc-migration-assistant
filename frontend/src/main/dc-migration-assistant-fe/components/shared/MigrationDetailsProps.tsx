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
import Tooltip from '@atlaskit/tooltip';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { CommandDetails as CommandResult } from '../../api/db';

type CommandResultProps = {
    result: CommandResult;
};
export const MigrationErrorSection: FunctionComponent<CommandResultProps> = ({
    result: commandResult,
}) => {
    return (
        <SectionMessage appearance="warning" title="Database migration error">
            <p>
                When running the database migration we have encountered error(s). Some of the errors
                are not necessary fatal and you can continue with migration. We recommend reviewing
                the errors and continue at your discretion. The error message is truncated to 8000
                characters
            </p>
            <pre>{commandResult.errorMessage.replace(/\n/g, '\n')}</pre>

            <Tooltip content="You need to be logged into AWS console">
                <Button href={commandResult.consoleUrl} target="_blank">
                    View logs in S3
                </Button>
            </Tooltip>
        </SectionMessage>
    );
};

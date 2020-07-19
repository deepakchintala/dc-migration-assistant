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
import Spinner from '@atlaskit/spinner';
import ProgressBar, { SuccessProgressBar } from '@atlaskit/progress-bar';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';

import { Progress } from './Progress';
import {
    calcualateDurationFromElapsedSeconds,
    calculateStartedFromElapsedSeconds,
} from './migration-timing';

const SectionMessageParagraph = styled.p`
    margin: 0;
    overflow-wrap: anywhere;
    hyphens: auto;
`;

const OperationTitle = styled.h4`
    margin-bottom: 12px;
`;

const OperationTimingParagraph = styled.p`
    margin: 2px 0 2px 0;
`;

export type MigrationProgressProps = {
    progress: Progress;
    /**
     * Should be true when the progress is being fetched
     */
    loading: boolean;
};

const LearnMoreLink =
    'https://confluence.atlassian.com/jirakb/how-to-use-the-data-center-migration-app-to-migrate-jira-to-an-aws-cluster-1005781495.html#HowtousetheDataCenterMigrationapptomigrateJiratoanAWScluster-errors';

/**
 * A component which renders the progress of the current transfer. It renders
 * a progress bar and elapsed time. It handles when the completeness is indeterminate.
 */
export const MigrationProgress: FunctionComponent<MigrationProgressProps> = ({
    progress,
    loading,
}) => {
    const failed = progress.errorMessage && true;

    if (loading) {
        return (
            <>
                <Spinner />
                <ProgressBar isIndeterminate />
                <Spinner />
            </>
        );
    }

    const duration = calcualateDurationFromElapsedSeconds(progress.elapsedTimeSeconds);

    return (
        <>
            {/* Error message when operation failed */}
            {failed && (
                <SectionMessage appearance="error">
                    <SectionMessageParagraph>
                        {progress.errorMessage}{' '}
                        <a target="_blank" rel="noreferrer noopener" href={LearnMoreLink}>
                            {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                        </a>
                    </SectionMessageParagraph>
                </SectionMessage>
            )}

            {/* Information message when operation succeeds */}
            {progress?.completeness === 1 && progress?.completeMessage && (
                <SectionMessage appearance="confirmation">
                    <strong>{progress.completeMessage.boldPrefix}</strong>{' '}
                    {progress.completeMessage.message}
                </SectionMessage>
            )}

            <>
                <OperationTitle>
                    {progress.phase}
                    {progress.completeness === undefined &&
                        ` (${I18n.getText('atlassian.migration.datacenter.common.estimating')}...)`}
                </OperationTitle>
                {progress.completeness !== undefined ? (
                    <SuccessProgressBar value={progress.completeness} />
                ) : (
                    <ProgressBar isIndeterminate />
                )}
                <OperationTimingParagraph>
                    {I18n.getText(
                        'atlassian.migration.datacenter.common.progress.started',
                        calculateStartedFromElapsedSeconds(progress.elapsedTimeSeconds).format(
                            'D/MMM/YY h:mm A'
                        )
                    )}
                </OperationTimingParagraph>
                <OperationTimingParagraph>
                    {duration &&
                        I18n.getText(
                            'atlassian.migration.datacenter.common.progress.mins_elapsed',
                            `${duration.hours}`,
                            `${duration.minutes}`,
                            `${duration.seconds}`
                        )}
                </OperationTimingParagraph>
            </>
        </>
    );
};

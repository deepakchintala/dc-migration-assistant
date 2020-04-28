import React, { ReactElement, FunctionComponent } from 'react';
import { Moment } from 'moment';
import SectionMessage from '@atlaskit/section-message';
import Spinner from '@atlaskit/spinner';
import ProgressBar, { SuccessProgressBar } from '@atlaskit/progress-bar';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';

import { Progress } from './Progress';
import {
    calculateDurationFromBeginning,
    calcualateDurationFromElapsedSeconds,
    calculateStartedFromElapsedSeconds,
} from './migration-timing';

type MigrationProgressProps = {
    progress: Progress;
    loading: boolean;
    startedMoment: Moment;
};

export const MigrationProgress: FunctionComponent<MigrationProgressProps> = ({
    progress,
    loading,
    startedMoment,
}) => {
    if (loading) {
        return (
            <>
                <Spinner />
                <ProgressBar isIndeterminate />
                <Spinner />
            </>
        );
    }

    const duration =
        calculateDurationFromBeginning(startedMoment) ||
        calcualateDurationFromElapsedSeconds(progress.elapsedTimeSeconds);

    return (
        <>
            {progress?.completeness === 1 && progress?.completeMessage && (
                <SectionMessage appearance="confirmation">
                    <strong>{progress.completeMessage.boldPrefix}</strong>{' '}
                    {progress.completeMessage.message}
                </SectionMessage>
            )}

            <>
                <h4>
                    {progress.phase}
                    {progress.completeness === undefined &&
                        ` (${I18n.getText('atlassian.migration.datacenter.common.estimating')}...)`}
                </h4>
                {progress.completeness ? (
                    <SuccessProgressBar value={progress.completeness} />
                ) : (
                    <ProgressBar isIndeterminate />
                )}
                <p>
                    {I18n.getText(
                        'atlassian.migration.datacenter.common.progress.started',
                        (
                            startedMoment ||
                            calculateStartedFromElapsedSeconds(progress.elapsedTimeSeconds)
                        ).format('D/MMM/YY h:mm A')
                    )}
                </p>
                <p>
                    {duration &&
                        I18n.getText(
                            'atlassian.migration.datacenter.common.progress.mins_elapsed',
                            `${duration.days * 24 + duration.hours}`,
                            `${duration.minutes}`
                        )}
                </p>
            </>
        </>
    );
};

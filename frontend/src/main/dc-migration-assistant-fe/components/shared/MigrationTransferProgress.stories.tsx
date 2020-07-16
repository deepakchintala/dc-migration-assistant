import React, { ReactNode } from 'react';
import { MigrationProgress } from './MigrationTransferProgress';
import { RetryProperties } from './Progress';

export default { title: 'Migration process progress' };

const retryText = 'Retry';
const retryProps: RetryProperties = {
    retryText,
    onRetry: (): Promise<void> => Promise.resolve(),
    canContinueOnFailure: false,
};
const defaultCompleteMessage = {
    boldPrefix: '180GB of data',
    message: 'were restored to the target database',
};

export const nonDeterministicProcess = (): ReactNode => (
    <MigrationProgress
        loading={false}
        progress={{
            elapsedTimeSeconds: 120,
            phase: 'Transferring your data. This may take a while...',
            retryProps: {
                retryText: 'Retry',
                onRetry: (): Promise<void> => Promise.resolve(),
                canContinueOnFailure: false,
            },
            completeMessage: defaultCompleteMessage,
        }}
    />
);

export const halfwayFinishedProcess = (): ReactNode => (
    <MigrationProgress
        loading={false}
        progress={{
            phase: 'Downloading data into new instance',
            completeness: 0.5,
            retryProps,
            elapsedTimeSeconds: 360,
            completeMessage: defaultCompleteMessage,
        }}
    />
);

export const failedProcess = (): ReactNode => (
    <MigrationProgress
        loading={false}
        progress={{
            phase: 'Failed to install data in new instance',
            completeness: 0.7,
            retryProps,
            elapsedTimeSeconds: 720,
            errorMessage: 'Unable to connect to target database',
            completeMessage: defaultCompleteMessage,
        }}
    />
);

export const finishedProcess = (): ReactNode => (
    <MigrationProgress
        loading={false}
        progress={{
            phase: 'Complete',
            completeness: 1,
            retryProps,
            elapsedTimeSeconds: 1080,
            completeMessage: defaultCompleteMessage,
        }}
    />
);

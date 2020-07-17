import React, { FunctionComponent } from 'react';
import { MigrationProgress } from './MigrationTransferProgress';

export default { title: 'Migration process progress' };

const defaultCompleteMessage = {
    boldPrefix: '180GB of data',
    message: 'were restored to the target database',
};

export const nonDeterministicProcess: FunctionComponent = () => (
    <MigrationProgress
        loading={false}
        progress={{
            elapsedTimeSeconds: 120,
            phase: 'Transferring your data. This may take a while...',
            completeMessage: defaultCompleteMessage,
        }}
    />
);

export const halfwayFinishedProcess: FunctionComponent = () => (
    <MigrationProgress
        loading={false}
        progress={{
            phase: 'Downloading data into new instance',
            completeness: 0.5,
            elapsedTimeSeconds: 360,
            completeMessage: defaultCompleteMessage,
        }}
    />
);

export const failedProcess: FunctionComponent = () => (
    <MigrationProgress
        loading={false}
        progress={{
            phase: 'Failed to install data in new instance',
            completeness: 0.7,
            elapsedTimeSeconds: 720,
            errorMessage: 'Unable to connect to target database',
            completeMessage: defaultCompleteMessage,
        }}
    />
);

export const finishedProcess: FunctionComponent = () => (
    <MigrationProgress
        loading={false}
        progress={{
            phase: 'Complete',
            completeness: 1,
            elapsedTimeSeconds: 1080,
            completeMessage: defaultCompleteMessage,
        }}
    />
);

import React, { ReactNode } from 'react';
import { MigrationTransferPage } from './MigrationTransferPage';
import { Progress } from './Progress';

export default { title: 'Migration process' };

export const notStarted = (): ReactNode => (
    <MigrationTransferPage
        heading="Not started migration process"
        description="This is a migration process which has not been started yet"
        nextText="Next"
        nextRoute="/"
        inProgressStages={[]}
        startMigrationPhase={(): Promise<void> => Promise.resolve()}
        getProgress={(): Promise<Progress[]> =>
            Promise.resolve([
                {
                    phase: 'Migrating content',
                    completeness: 0.3,
                    elapsedTimeSeconds: 240,
                    retryProps: {
                        retryText: 'Retry',
                        onRetry: (): Promise<void> => Promise.resolve(),
                        canContinueOnFailure: false,
                    },
                },
            ])
        }
    />
);

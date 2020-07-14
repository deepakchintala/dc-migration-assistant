import React, { ReactNode } from 'react';
import { MemoryRouter as Router } from 'react-router-dom';
import { MigrationTransferPage } from './MigrationTransferPage';
import { Progress } from './Progress';

export default { title: 'Migration process' };

let progressFetchCount = 0;
const finishedAfterCalls = 3;

export const happyPath = (): ReactNode => (
    <Router>
        <MigrationTransferPage
            heading="Migrate content"
            description="This is a migration process which has not been started yet. Once you click start, it will finish after three ticks."
            nextText="Next step"
            nextRoute="/"
            inProgressStages={[]}
            startMigrationPhase={(): Promise<void> =>
                new Promise(resolve => setTimeout(resolve, 1000))
            }
            getProgress={(): Promise<Progress[]> => {
                const retryProps = {
                    retryText: 'Retry',
                    onRetry: (): Promise<void> => Promise.resolve(),
                    canContinueOnFailure: false,
                };

                if (progressFetchCount === 3) {
                    return Promise.resolve([
                        {
                            phase: 'Completed content migration',
                            completeness: 1,
                            elapsedTimeSeconds: 18,
                            retryProps,
                            completeMessage: {
                                boldPrefix: '25 of 25 files',
                                message: 'were successfully migrated',
                            },
                        },
                    ]);
                }
                const result = Promise.resolve([
                    {
                        phase: 'Migrating content',
                        completeness: progressFetchCount / finishedAfterCalls,
                        elapsedTimeSeconds: 6 * progressFetchCount,
                        retryProps,
                    },
                ]);

                if (progressFetchCount !== 3) {
                    progressFetchCount += 1;
                }

                return result;
            }}
        />
    </Router>
);

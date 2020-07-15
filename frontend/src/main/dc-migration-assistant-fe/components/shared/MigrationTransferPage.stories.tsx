import React, { ReactNode } from 'react';
import { MemoryRouter as Router } from 'react-router-dom';
import { MigrationTransferPage } from './MigrationTransferPage';
import { Progress } from './Progress';
import { MigrationStage } from '../../api/migration';

export default { title: 'Migration step' };

const finishedAfterCalls = 3;

const storybookTransferPageDefaultProps = {
    heading: 'Migrate content',
    nextText: 'Next step',
    nextRoute: '/',
    inProgressStages: new Array<MigrationStage>(),
    startMigrationPhase: (): Promise<void> => {
        return new Promise(resolve => setTimeout(resolve, 1000));
    },
};

export const happyPath = (): ReactNode => {
    let progressFetchCount = 0;

    return (
        <Router>
            <MigrationTransferPage
                {...storybookTransferPageDefaultProps}
                description="This is a migration process which has not been started yet. Once you click start, it will finish after three ticks."
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
};

export const unhappyPath = (): ReactNode => {
    let progressFetchCount = 0;

    return (
        <Router>
            <MigrationTransferPage
                {...storybookTransferPageDefaultProps}
                description="This is a migration process which has not been started yet. Once you click start, It will update twice, then fail."
                getProgress={(): Promise<Progress[]> => {
                    const retryProps = {
                        retryText: 'Retry',
                        onRetry: (): Promise<void> => {
                            progressFetchCount = 0;
                            return Promise.resolve();
                        },
                        canContinueOnFailure: false,
                    };

                    if (progressFetchCount === 3) {
                        return Promise.resolve([
                            {
                                phase: 'Failed to migrate content',
                                completeness: 0.7,
                                elapsedTimeSeconds: 18,
                                retryProps,
                                errorMessage:
                                    'Something went wrong while running your migration. Retry and see if it improves things',
                                failed: true,
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
};

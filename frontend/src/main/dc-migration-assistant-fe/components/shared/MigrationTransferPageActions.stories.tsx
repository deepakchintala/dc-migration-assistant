import React, { ReactNode } from 'react';
import { MemoryRouter as Router } from 'react-router-dom';
import { MigrationTransferActions } from './MigrationTransferPageActions';

export default { title: 'Migration step actions' };

const doNothingAsync = (): Promise<void> => Promise.resolve();

const transferActionsDefaultProps = {
    onRefresh: doNothingAsync,
    nextText: 'Next step',
    nextRoute: '/',
    startButtonText: 'Start me',
    startMigrationPhase: doNothingAsync,
};

export const start = (): ReactNode => (
    <MigrationTransferActions
        {...transferActionsDefaultProps}
        state="not_started"
        loading={false}
    />
);

export const inProgress = (): ReactNode => (
    <MigrationTransferActions
        {...transferActionsDefaultProps}
        state="in_progress"
        loading={false}
    />
);

export const finished = (): ReactNode => (
    <Router>
        <MigrationTransferActions
            {...transferActionsDefaultProps}
            state="finished"
            loading={false}
        />
    </Router>
);

export const loading = (): ReactNode => (
    <MigrationTransferActions {...transferActionsDefaultProps} state="not_started" loading={true} />
);

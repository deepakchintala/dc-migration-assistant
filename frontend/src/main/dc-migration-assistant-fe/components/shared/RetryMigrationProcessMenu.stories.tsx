import React, { FunctionComponent } from 'react';
import { RetryMenu } from './RetryMigrationProcessMenu';

export default { title: 'Retry menu' };

export const mustRetry: FunctionComponent = () => (
    <RetryMenu
        retryText="Retry"
        // eslint-disable-next-line no-alert
        onRetry={(): any => alert('Okay, retrying!')}
        canContinueOnFailure={false}
    />
);

export const mayContinue: FunctionComponent = () => (
    <RetryMenu
        retryText="Retry"
        // eslint-disable-next-line no-alert
        onRetry={(): any => alert('Okay, retrying!')}
        canContinueOnFailure={true}
        continuePath="?path=/story/migration-step--happy-path"
    />
);

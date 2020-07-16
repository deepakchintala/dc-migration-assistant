import { ProgressCallback } from './Progress';

export interface RetryCallback {
    (): Promise<void>;
}

export type RetryProperties = {
    /**
     * Text to display on the retry button
     */
    retryText: string;
    /**
     * A callback which will retry the operation
     */
    onRetry: RetryCallback;
    /**
     * A route to redirect the user to when
     */
    onRetryRoute?: string;
    /**
     * A boolean to dictate whether or not failures
     * can be ignored and the migration continued
     */
    canContinueOnFailure: boolean;
};

export interface MigrationProcess {
    getProgress: ProgressCallback;
    retryProps: RetryProperties;
}

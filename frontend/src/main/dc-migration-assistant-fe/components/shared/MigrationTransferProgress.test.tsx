import React from 'react';
import { render } from '@testing-library/react';
import { MigrationProgress } from './MigrationTransferProgress';

const defaultCompleteMessage = {
    boldPrefix: '180GB of database',
    message: 'were transferred successfully',
};

describe('migration process progress', () => {
    it('Should render indeterminate progress bar when completeness is not given', () => {
        const { container } = render(
            <MigrationProgress
                loading={false}
                progress={{
                    elapsedTimeSeconds: 120,
                    phase: 'Transferring your data. This may take a while...',
                    completeMessage: defaultCompleteMessage,
                }}
            />
        );

        expect(container).toMatchSnapshot();
    });

    it('Should render a partially completed progress bar with timing data when completeness is between 0 and 1', () => {
        const { container } = render(
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

        expect(container).toMatchSnapshot();
    });

    it('Should render an error message when one is given', () => {
        const { container } = render(
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

        expect(container).toMatchSnapshot();
    });

    it('Should render a full progress bar when process completeness is 1', () => {
        const { container } = render(
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

        expect(container).toMatchSnapshot();
    });
});

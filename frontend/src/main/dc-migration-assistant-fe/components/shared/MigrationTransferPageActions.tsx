import React, { FunctionComponent } from 'react';
import Button from '@atlaskit/button';

type MigrationTransferActionsProps = {
    updateTransferProgress: () => Promise<void>;
    completeness: number;
    nextText: string;
    startMigrationPhase: () => Promise<void>;
    started: boolean;
    loading: boolean;
};

export const MigrationTransferActions: FunctionComponent<MigrationTransferActionsProps> = ({
    completeness,
    nextText,
    startMigrationPhase,
    updateTransferProgress,
    started,
    loading,
}) => {
    const defaultButtonStyle = {
        padding: '5px',
    };
    const marginButtonStyle = {
        ...defaultButtonStyle,
        marginRight: '20px',
    };

    if (completeness === 1) {
        return (
            <Button style={defaultButtonStyle} appearance="primary">
                {nextText}
            </Button>
        );
    }
    if (started) {
        return (
            <Button style={marginButtonStyle} isLoading={loading} onClick={updateTransferProgress}>
                Refresh
            </Button>
        );
    }
    return (
        <Button
            style={marginButtonStyle}
            isLoading={loading}
            appearance="primary"
            onClick={startMigrationPhase}
        >
            Start
        </Button>
    );
};

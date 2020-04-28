import React, { FunctionComponent } from 'react';
import Button from '@atlaskit/button';
import { Link } from 'react-router-dom';
import { I18n } from '@atlassian/wrm-react-i18n';
import { overviewPath } from '../../utils/RoutePaths';

type MigrationTransferActionsProps = {
    updateTransferProgress: () => Promise<void>;
    finished: boolean;
    nextText: string;
    startMigrationPhase: () => Promise<void>;
    started: boolean;
    loading: boolean;
};

export const MigrationTransferActions: FunctionComponent<MigrationTransferActionsProps> = ({
    finished,
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

    const CancelButton = (
        <Link to={overviewPath}>
            <Button style={{ marginLeft: '20px', paddingLeft: '5px' }}>
                {I18n.getText('atlassian.migration.datacenter.generic.cancel')}
            </Button>
        </Link>
    );

    const StartButton = (
        <Button
            style={marginButtonStyle}
            isLoading={loading}
            appearance="primary"
            onClick={startMigrationPhase}
        >
            Start
        </Button>
    );

    let ActionButton = StartButton;

    if (finished) {
        const NextButton = (
            <Button style={defaultButtonStyle} appearance="primary">
                {nextText}
            </Button>
        );
        ActionButton = NextButton;
    }
    if (started) {
        const RefreshButton = (
            <Button style={marginButtonStyle} isLoading={loading} onClick={updateTransferProgress}>
                Refresh
            </Button>
        );
        ActionButton = RefreshButton;
    }

    return (
        <>
            {ActionButton}
            {CancelButton}
        </>
    );
};

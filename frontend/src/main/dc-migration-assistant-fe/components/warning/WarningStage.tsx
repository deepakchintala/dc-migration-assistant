import React, { FunctionComponent, useState } from 'react';
import SectionMessage from '@atlaskit/section-message';
import { Checkbox } from '@atlaskit/checkbox';
import Button from '@atlaskit/button';
import { Redirect } from 'react-router-dom';
import { dbPath } from '../../utils/RoutePaths';
import { I18n } from '../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelButton, marginButtonStyle } from '../shared/CancelButton';

export const WarningStagePage: FunctionComponent = () => {
    const [agreed, setAgreed] = useState<boolean>(false);
    const [shouldRedirect, setShouldRedirect] = useState<boolean>(false);

    const handleConfirmation = (): void => {
        if (agreed) {
            setShouldRedirect(true);
        }
    };

    const agreeOnClick = (event: any): void => {
        setAgreed(event.target.checked);
    };

    if (shouldRedirect) {
        return <Redirect to={dbPath} push />;
    }

    const NextButton = (
        <Button
            isDisabled={!agreed}
            onClick={handleConfirmation}
            appearance="primary"
            css={marginButtonStyle}
        >
            {I18n.getText('atlassian.migration.datacenter.generic.next')}
        </Button>
    );

    const checkboxSectionStyle = {
        margin: '20px',
    };

    return (
        <div>
            <h1>{I18n.getText('atlassian.migration.datacenter.warning.title')}</h1>
            <p>{I18n.getText('atlassian.migration.datacenter.warning.description')}</p>
            <SectionMessage
                appearance="info"
                title={I18n.getText('atlassian.migration.datacenter.warning.section.header')}
            >
                <ol>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.warning.section.list.loggedOutUsers'
                        )}
                    </li>
                    <li>
                        {I18n.getText(
                            'atlassian.migration.datacenter.warning.section.list.dnsRedirection'
                        )}
                    </li>
                </ol>
            </SectionMessage>
            <div style={checkboxSectionStyle}>
                <Checkbox
                    value="agree"
                    label="I'm ready for the next step"
                    onChange={agreeOnClick}
                    name="agree"
                />
            </div>
            {NextButton}
            <CancelButton css={marginButtonStyle} />
        </div>
    );
};

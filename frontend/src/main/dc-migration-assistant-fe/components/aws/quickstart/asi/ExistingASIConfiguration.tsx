import { ButtonGroup } from '@atlaskit/button';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { HelperMessage } from '@atlaskit/form';
import { RadioGroup } from '@atlaskit/radio';
import SectionMessage from '@atlaskit/section-message';
import { AsyncSelect, OptionType } from '@atlaskit/select';
import TextField from '@atlaskit/textfield';
import React, { FunctionComponent, useState } from 'react';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';

import { Redirect } from 'react-router-dom';
import { CancelButton } from '../../../shared/CancelButton';
import { quickstartPath } from '../../../../utils/RoutePaths';

const radioValues = [
    {
        name: 'deploymentMode',
        value: 'existing',
        label: I18n.getText('atlassian.migration.datacenter.provision.aws.asi.option.existing'),
    },
    {
        name: 'deploymentMode',
        value: 'new',
        label: I18n.getText('atlassian.migration.datacenter.provision.aws.asi.option.new'),
    },
];

const RequiredStar = styled.span`
    color: #de350b;
`;

const ContentContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 920px;
    margin-right: auto;
    margin-bottom: auto;
    padding-left: 15px;
`;

const Description = styled.p`
    width: 90%;
    margin-bottom: 15px;
`;

const ASISelectorContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%
    max-width: 500px;
    margin-right: auto;
    margin-bottom: auto;
    margin-top: 15px;
`;

const ButtonRow = styled.div`
    margin: 30px 0px 0px 0px;
`;

const asyncASIPrefixOptions = (): Promise<Array<OptionType>> =>
    // FIXME: example options until there is API call to list ASI's
    Promise.resolve([
        { label: 'ATL-', value: 'ATL-', key: 'ATL-' },
        { label: 'BP-', value: 'BP-', key: 'BP-' },
    ]);

type ExistingASIConfigurationProps = {
    handleASIPrefixSet: (prefix: string) => Promise<void>;
};

export const ExistingASIConfiguration: FunctionComponent<ExistingASIConfigurationProps> = ({
    handleASIPrefixSet,
}) => {
    const [useExisting, setUseExisting] = useState<boolean>(true);
    const [prefix, setPrefix] = useState<string>('');
    const [readyForNextStep, setReadyForNextStep] = useState<boolean>();

    const handleSubmit = (): void => {
        handleASIPrefixSet(prefix).then(() => {
            setReadyForNextStep(true);
        });
    };

    if (readyForNextStep) {
        return <Redirect to={quickstartPath} push />;
    }

    return (
        <ContentContainer>
            <h1>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.title')}</h1>
            <Description>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.description')}{' '}
                <a href="https://aws.amazon.com/quickstart/architecture/atlassian-standard-infrastructure/">
                    {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                </a>
            </Description>
            <SectionMessage appearance="info">
                <p>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.found')}</p>
            </SectionMessage>
            <h5>
                {I18n.getText(
                    'atlassian.migration.datacenter.provision.aws.asi.chooseDeploymentMethod.label'
                )}
                <RequiredStar>*</RequiredStar>
            </h5>
            <RadioGroup
                options={radioValues}
                defaultValue={radioValues[0].value}
                onChange={(event): void => {
                    setUseExisting(event.currentTarget.value === 'existing');
                    setPrefix('');
                }}
            />
            <ASISelectorContainer>
                <h5>
                    {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.prefix')}
                    <RequiredStar>*</RequiredStar>
                </h5>
                {useExisting ? (
                    <AsyncSelect
                        className="asi-select"
                        cacheOptions
                        defaultOptions
                        loadOptions={asyncASIPrefixOptions}
                        data-test="asi-select"
                        onChange={(event: OptionType): void => setPrefix(event.value.toString())}
                    />
                ) : (
                    <TextField
                        placeholder="ATL-"
                        width="xlarge"
                        value={prefix}
                        onChange={(event): void => setPrefix(event.currentTarget.value)}
                    />
                )}
                <HelperMessage>
                    {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.details')}
                </HelperMessage>
                <ButtonRow>
                    <ButtonGroup>
                        <Button
                            onClick={handleSubmit}
                            type="submit"
                            appearance="primary"
                            data-test="asi-submit"
                        >
                            Next
                        </Button>
                        <CancelButton />
                    </ButtonGroup>
                </ButtonRow>
            </ASISelectorContainer>
        </ContentContainer>
    );
};

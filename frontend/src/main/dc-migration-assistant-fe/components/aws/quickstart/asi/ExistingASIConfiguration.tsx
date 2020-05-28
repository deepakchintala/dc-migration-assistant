/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { RadioGroup } from '@atlaskit/radio';
import { HelperMessage } from '@atlaskit/form';
import SectionMessage from '@atlaskit/section-message';
import { AsyncSelect, OptionType } from '@atlaskit/select';
import TextField from '@atlaskit/textfield';
import React, { FunctionComponent, useState } from 'react';
import styled from 'styled-components';
import { I18n } from '@atlassian/wrm-react-i18n';

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

const ASISelectorContainer = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%
    max-width: 500px;
    margin-right: auto;
    margin-bottom: auto;
    margin-top: 15px;
`;

const asyncASIPrefixOptions = (): Promise<Array<OptionType>> =>
    // FIXME: example options until there is API call to list ASI's
    Promise.resolve([
        { label: 'ATL-', value: 'ATL-', key: 'ATL-' },
        { label: 'BP-', value: 'BP-', key: 'BP-' },
    ]);

type ExistingASIConfigurationProps = {
    handleASIPrefixSet: (prefix: string) => void;
};

type ASISelectorProps = {
    useExisting: boolean;
    handlePrefixUpdated: (prefix: string) => void;
};

export const ASISelector: FunctionComponent<ASISelectorProps> = ({
    useExisting,
    handlePrefixUpdated,
}) => {
    return (
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
                    onChange={(event: OptionType): void =>
                        handlePrefixUpdated(event.value.toString())
                    }
                />
            ) : (
                <TextField
                    placeholder="ATL-"
                    width="xlarge"
                    onChange={(event): void => handlePrefixUpdated(event.currentTarget.value)}
                />
            )}
            <HelperMessage>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.details')}
            </HelperMessage>
        </ASISelectorContainer>
    );
};

export const ExistingASIConfiguration: FunctionComponent<ExistingASIConfigurationProps> = ({
    handleASIPrefixSet,
}) => {
    const [useExisting, setUseExisting] = useState<boolean>(true);

    return (
        <>
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
                    handleASIPrefixSet('');
                    setUseExisting(event.currentTarget.value === 'existing');
                }}
            />
            <ASISelector useExisting={useExisting} handlePrefixUpdated={handleASIPrefixSet} />
        </>
    );
};

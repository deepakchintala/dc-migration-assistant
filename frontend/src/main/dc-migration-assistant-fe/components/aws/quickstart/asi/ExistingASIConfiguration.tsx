import { ButtonGroup } from '@atlaskit/button';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { HelperMessage } from '@atlaskit/form';
import { RadioGroup } from '@atlaskit/radio';
import SectionMessage from '@atlaskit/section-message';
import { AsyncSelect, OptionType } from '@atlaskit/select';
import TextField from '@atlaskit/textfield';
import React, { FunctionComponent, useState } from 'react';
import styled from 'styled-components';

import { CancelButton } from '../../../shared/CancelButton';

const radioValues = [
    { name: 'deploymentMode', value: 'existing', label: 'Existing ASI' },
    { name: 'deploymentMode', value: 'new', label: 'New ASI' },
];

const RequiredStar = styled.span`
    color: #de350b;
`;

const ButtonRow = styled.div`
    margin: 15px 0px 0px 10px;
`;

const asyncASIPrefixOptions = (): Promise<Array<OptionType>> =>
    Promise.resolve([
        { label: 'ATL-', value: 'ATL-', key: 'ATL-' },
        { label: 'BP-', value: 'BP-', key: 'BP-' },
    ]);

export const ExistingASIConfiguration: FunctionComponent = () => {
    const [useExisting, setUseExisting] = useState<boolean>(true);
    const [prefix, setPrefix] = useState<string>('');

    const handleSubmit = (): void => {
        console.log(`ASI prefix is: ${prefix}`);
    };

    return (
        <div>
            <h1>Step 2 of 7: Configure ASI</h1>
            <p>
                The Atlassian Standard Infrastructure (ASI) is a virtual private cloud specifically
                customized to host Atlassian Data Center products.{' '}
                <a href="www.atlassian.com">Learn more</a>
            </p>
            <SectionMessage appearance="info">
                <p>
                    We found an existing ASI in your region. You acn deploy to this ASI, or create a
                    new one
                </p>
            </SectionMessage>
            <h5>
                Deploy to<RequiredStar>*</RequiredStar>
            </h5>
            <RadioGroup
                options={radioValues}
                defaultValue={radioValues[0].value}
                onChange={(event): void => {
                    setUseExisting(event.currentTarget.value === 'existing');
                    setPrefix('');
                }}
            />
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
                Identifier used in all variables (VPCID, SubnetIDs, KeyName) exported from this
                deployment&apos;s Atlassian Standard Infrastructure. Use different identifiers if
                you&apos;re deploying multiple Atlassian Standard Infrastructures in the same AWS
                region. Format is 3 upper case letters followed by ”-“.
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
        </div>
    );
};

import React, { FunctionComponent, ReactNode } from 'react';
import SectionMessage from '@atlaskit/section-message';
import Form, { Field, HelperMessage } from '@atlaskit/form';
import { RadioGroup } from '@atlaskit/radio';
import TextField from '@atlaskit/textfield';
import styled from 'styled-components';
import { ButtonGroup } from '@atlaskit/button';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { CancelButton } from '../../../shared/CancelButton';

const radioValues = [
    { name: 'deploymentMode', value: 'existing', label: 'Existing ASI' },
    { name: 'deploymentMode', value: 'new', label: 'New ASI' },
];

type ExistingASIConfigurationFormData = {
    deploymentMode: 'existing' | 'new';
    ASIIdentifier: string;
};

const RequiredStar = styled.span`
    color: #de350b;
`;

const ButtonRow = styled.div`
    margin: 15px 0px 0px 10px;
`;

export const ExistingASIConfiguration: FunctionComponent = () => {
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
            <RadioGroup options={radioValues} defaultValue={radioValues[0].value} />
            <Form<ExistingASIConfigurationFormData>
                onSubmit={(data): void => console.log('frm data', data)}
            >
                {({ formProps }): ReactNode => (
                    <form {...formProps}>
                        <Field name="ASIIdentifier" label="ASI identifier" isRequired>
                            {({ fieldProps }): ReactNode => (
                                <>
                                    <TextField placeholder="ATL-" width="xlarge" {...fieldProps} />
                                    <HelperMessage>
                                        Identifier used in all variables (VPCID, SubnetIDs, KeyName)
                                        exported from this deployment&apos;s Atlassian Standard
                                        Infrastructure. Use different identifiers if you&apos;re
                                        deploying multiple Atlassian Standard Infrastructures in the
                                        same AWS region. Format is 3 upper case letters followed by
                                        ”-“.
                                    </HelperMessage>
                                </>
                            )}
                        </Field>
                        <ButtonRow>
                            <ButtonGroup>
                                <Button type="submit" appearance="primary" data-test="asi-submit">
                                    Next
                                </Button>
                                <CancelButton />
                            </ButtonGroup>
                        </ButtonRow>
                    </form>
                )}
            </Form>
        </div>
    );
};

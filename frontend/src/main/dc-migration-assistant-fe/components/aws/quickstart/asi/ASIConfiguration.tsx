import React, { FunctionComponent, useState } from 'react';
import styled from 'styled-components';
import { ButtonGroup } from '@atlaskit/button';
import { Button } from '@atlaskit/button/dist/cjs/components/Button';
import { ExistingASIConfiguration, ASISelector } from './ExistingASIConfiguration';
import { I18n } from '../../../../atlassian/mocks/@atlassian/wrm-react-i18n';
import { CancelButton } from '../../../shared/CancelButton';

type ASIConfigurationProps = {
    ASIExists: boolean;
};

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

const ButtonRow = styled.div`
    margin: 30px 0px 0px 0px;
`;

export const ASIConfiguration: FunctionComponent<ASIConfigurationProps> = ({ ASIExists }) => {
    const [prefix, setPrefix] = useState<string>('');

    const handleSubmit = (): void => {
        console.log(`Prefix is ${prefix}`);
    };

    const updatePRefix = (newPrefix: string): void => setPrefix(newPrefix);

    return (
        <ContentContainer>
            <h1>{I18n.getText('atlassian.migration.datacenter.provision.aws.asi.title')}</h1>
            <Description>
                {I18n.getText('atlassian.migration.datacenter.provision.aws.asi.description')}{' '}
                <a href="https://aws.amazon.com/quickstart/architecture/atlassian-standard-infrastructure/">
                    {I18n.getText('atlassian.migration.datacenter.common.learn_more')}
                </a>
            </Description>

            {ASIExists ? (
                <ExistingASIConfiguration handleASIPrefixSet={updatePRefix} />
            ) : (
                <ASISelector useExisting={false} handlePrefixUpdated={updatePRefix} />
            )}
            <ButtonRow>
                <ButtonGroup>
                    <Button
                        onClick={handleSubmit}
                        type="submit"
                        appearance="primary"
                        data-test="asi-submit"
                    >
                        {I18n.getText('atlassian.migration.datacenter.generic.next')}
                    </Button>
                    <CancelButton />
                </ButtonGroup>
            </ButtonRow>
        </ContentContainer>
    );
};

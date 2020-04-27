import styled, { keyframes } from 'styled-components';
import React, { FunctionComponent, ReactElement } from 'react';
import Flag from '@atlaskit/flag';
import ErrorIcon from '@atlaskit/icon/glyph/error';
import { colors } from '@atlaskit/theme';

type AuthenticationErrorProps = {
    /**
     * whether the flag should be rendered or not
     */
    showError: boolean;
    /**
     * callback that will be invoked when the "dimiss" button on the flag is clicked
     */
    dismissErrorFunc: () => void;

    /**
     * Title for the flag. Should describe the error
     */
    title: string;
    /**
     * Text that appears below the flag. Should be a call to action on how to resolve the error
     */
    description: string;
    /**
     * Unique identifier for the error
     */
    id: string;
};

const ErrorContainer = styled.div`
    position: fixed;
    top: 70px;
    left: 75%;
    right: 1%;
    overflow: inherit;
`;

export const ErrorFlag: FunctionComponent<AuthenticationErrorProps> = ({
    showError,
    dismissErrorFunc,
    title,
    description,
    id,
}): ReactElement => {
    if (showError) {
        return (
            <ErrorContainer>
                <Flag
                    actions={[
                        {
                            content: 'Dismiss',
                            onClick: dismissErrorFunc,
                        },
                    ]}
                    icon={<ErrorIcon primaryColor={colors.R400} label="Info" />}
                    description={description}
                    id={id}
                    key={id}
                    title={title}
                />
            </ErrorContainer>
        );
    }
    return null;
};

import React, { useState, FunctionComponent } from 'react';

import Button from '@atlaskit/button';

import Modal, { ModalTransition } from '@atlaskit/modal-dialog';

export const DatabaseWarningModal: FunctionComponent = () => {
    const [isOpen, setIsOpen] = useState<boolean>(true);

    const close = (): void => {
        setIsOpen(false);
    };

    const actions = [{ text: 'Close', onClick: close }];

    return (
        <div>
            <ModalTransition>
                {isOpen && (
                    <Modal
                        actions={actions}
                        onClose={close}
                        heading="Block user access"
                        shouldCloseOnOverlayClick={false}
                        shouldCloseOnEscapePress={false}
                    >
                        <p>Before you proceed with database sync</p>
                        <p>To block user access - convert to info box</p>
                    </Modal>
                )}
            </ModalTransition>
        </div>
    );
};

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

import { ReactNode } from 'react';

/**
 * **boldPrefix** - text that will be at the beginning of the message in bold. This should be used
 * to communicate *how much* data has been migrated
 *
 * **message** - the remainder of the complete text
 */
export type CompleteMessage = {
    boldPrefix: string;
    message: string;
};

/**
 * **phase**: Text describing the current phase of the transfer e.g. uploading, downloading, importing..
 * **completeness**: A fraction representing the percentage complete the transfer is.
 * **elapsedTimeSeconds**: The number of seconds the transfer has been going on for.
 * **errorMessage**: A React node displaying the error content to the user in the error section message.
 *            Note that a raw string is a valid React node, ReactNode is the type simply so more complex
 *            errors can be rendered.
 *            Absence implies no errors occured and results in no error section message being rendered.
 * **completeMessage**: Message to display when the transfer has completed. Will be rendered when completeness === 1.
 */
export type Progress = {
    phase: string;
    elapsedTimeSeconds: number;
    completeness?: number;
    errorMessage?: ReactNode;
    completeMessage?: CompleteMessage;
};

/**
 * Builder or creating Progress objects. You must at least set a phase.
 */
export class ProgressBuilder {
    private phase: string;

    private completeness: number;

    private errorMessage: ReactNode;

    private completeMessage: CompleteMessage;

    private elapsedSeconds: number;

    setElapsedSeconds(seconds: number): ProgressBuilder {
        this.elapsedSeconds = seconds;
        return this;
    }

    setPhase(phase: string): ProgressBuilder {
        this.phase = phase;
        return this;
    }

    setCompleteness(completeness: number): ProgressBuilder {
        this.completeness = completeness;
        return this;
    }

    setError(error: ReactNode): ProgressBuilder {
        this.errorMessage = error;
        return this;
    }

    setCompleteMessage(boldPrefix: string, message: string): ProgressBuilder {
        this.completeMessage = {
            boldPrefix,
            message,
        };
        return this;
    }

    build(): Progress {
        if (!(this.phase && this.elapsedSeconds)) {
            throw new Error('must include phase and retry props in progress object');
        }

        const { phase, completeMessage, completeness, errorMessage, elapsedSeconds } = this;

        return {
            phase,
            completeMessage,
            completeness,
            errorMessage,
            elapsedTimeSeconds: elapsedSeconds,
        };
    }
}

export interface ProgressCallback {
    (): Promise<Progress>;
}

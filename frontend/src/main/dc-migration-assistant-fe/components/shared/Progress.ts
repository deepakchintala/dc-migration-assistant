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

export type Progress = {
    phase: string;
    completeness?: number;
    error?: string;
    completeMessage?: CompleteMessage;
};

/**
 * Builder or creating Progress objects. You must at least set a phase.
 */
export class ProgressBuilder {
    phase: string;

    completeness: number;

    error: string;

    completeMessage: CompleteMessage;

    setPhase(phase: string): ProgressBuilder {
        this.phase = phase;
        return this;
    }

    setCompleteness(completeness: number): ProgressBuilder {
        this.completeness = completeness;
        return this;
    }

    setError(error: string): ProgressBuilder {
        this.error = error;
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
        if (!this.phase) {
            throw new Error('must include phase in progress object');
        }

        const { phase, completeMessage, completeness, error } = this;

        return {
            phase,
            completeMessage,
            completeness,
            error,
        };
    }
}

export interface ProgressCallback {
    (): Promise<Progress>;
}

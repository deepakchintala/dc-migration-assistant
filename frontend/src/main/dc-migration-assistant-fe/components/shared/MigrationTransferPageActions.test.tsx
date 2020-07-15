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

import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter as Router } from 'react-router-dom';
import { MigrationTransferActions } from './MigrationTransferPageActions';

describe('Migration step action', () => {
    const startFunction = jest.fn();
    const startButtonText = 'Start';
    const StartButton = (
        <Router>
            <MigrationTransferActions
                state="not_started"
                onRefresh={(): Promise<void> => Promise.resolve()}
                nextText="Next"
                nextRoute="/"
                startButtonText={startButtonText}
                startMigrationPhase={startFunction}
                loading={false}
            />
        </Router>
    );

    it('should render start button when migration step is not started', () => {
        const { getByText } = render(StartButton);

        expect(getByText(startButtonText)).toBeTruthy();
    });

    it('should call the start function when start button is clicked', () => {
        const { getByText } = render(StartButton);

        getByText(startButtonText).click();

        expect(startFunction).toBeCalled();
    });

    const refreshFunction = jest.fn();
    const refreshButtonText = 'atlassian.migration.datacenter.generic.refresh';
    const RefreshButton = (
        <Router>
            <MigrationTransferActions
                state="in_progress"
                onRefresh={refreshFunction}
                nextText="Next"
                nextRoute="/"
                startButtonText="Start"
                startMigrationPhase={(): Promise<void> => Promise.resolve()}
                loading={false}
            />
        </Router>
    );
    it('should render refresh button when migration step is in progress', () => {
        const { getByText } = render(RefreshButton);

        expect(getByText(refreshButtonText)).toBeTruthy();
    });

    it('should call the refresh function when the refresh button is clicked', () => {
        const { getByText } = render(RefreshButton);

        getByText(refreshButtonText).click();

        expect(refreshFunction).toBeCalled();
    });

    const nextRoute = '/next';
    const nextButtonText = 'Next';
    const NextButton = (
        <Router>
            <MigrationTransferActions
                state="finished"
                onRefresh={(): Promise<void> => Promise.resolve()}
                nextText={nextButtonText}
                nextRoute={nextRoute}
                startButtonText="Start"
                startMigrationPhase={(): Promise<void> => Promise.resolve()}
                loading={false}
            />
        </Router>
    );

    it('should render the next button when migration step is finished', () => {
        const { getByText } = render(NextButton);

        expect(getByText(nextButtonText)).toBeTruthy();
    });

    it('should redirect to the given next route when clicking next', () => {
        // TODO
    });
});

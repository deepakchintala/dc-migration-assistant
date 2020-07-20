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

/// <reference types="Cypress" />

import * as jira from '../support';

// Set these externally via e.g:
//
//     export CYPRESS_AWS_ACCESS_KEY_ID=xxxx
//     export CYPRESS_AWS_SECRET_ACCESS_KEY='yyyyyy'
//
const getAwsTokens = (): [string, string] => {
    return [Cypress.env('AWS_ACCESS_KEY_ID'), Cypress.env('AWS_SECRET_ACCESS_KEY')];
};

describe('Migration plugin', () => {
    const ctx = jira.compose_context;
    const region = 'ap-southeast-2';
    const testId = Math.random().toString(36).substring(2, 8);

    before(() => {
        cy.on('uncaught:exception', (err, runnable) => false);
        cy.jira_login(ctx, 'admin', 'admin');
        cy.reset_migration(ctx);
    });

    it('Run full migration', () => {
        cy.visit(ctx.migrationHome);
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq(
                ctx.context + '/plugins/servlet/dc-migration-assistant/home'
            );
        });
        cy.get('[data-test=start-migration]').should('exist').click();

        // AWS auth page.
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq(
                ctx.context + '/plugins/servlet/dc-migration-assistant/aws/auth'
            );
        });

        cy.get('[data-test=aws-auth-key]').type(Cypress.env('AWS_ACCESS_KEY_ID'));
        cy.get('[data-test=aws-auth-secret]').type(Cypress.env('AWS_SECRET_ACCESS_KEY'));
        // FIXME: This may be flaky; the AtlasKit AsyncSelect
        // component is hard to instrument.
        cy.get('#region-uid3').click();
        cy.get(`[id^=react-select]:contains(${region})`).click();

        cy.get('[data-test=aws-auth-submit]').should('exist').click();

        // ASI config page.
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq(
                ctx.context + '/plugins/servlet/dc-migration-assistant/aws/asi'
            );
        });
    });
});

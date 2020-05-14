/// <reference types="Cypress" />
import * as jira from '../support';
import * as scenarios from '../support/scenarios';

describe('Database Migration page', () => {
    beforeEach(() => {
        cy.on('uncaught:exception', (err, runnable) => false);

        Cypress.on('window:before:load', (win) => {
            delete win.fetch
        })
        cy.server();

//        cy.jira_login('admin', 'admin');
    });

    it('End to end mocked', () => {

        scenarios.mock_end2end(jira.devserver_context);

    });

});

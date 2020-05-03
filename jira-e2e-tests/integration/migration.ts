/// <reference types="Cypress" />

import * as jira from '../support';

const base = jira.baseURL+'/plugins/servlet/dc-migration-assistant';
const home = base;
const awsAuth = base+'/aws/auth';

const reset = jira.baseURL+'/rest/dc-migration/1.0/develop/migration/reset';

describe('Database Migration page', () => {
    beforeEach(() => {
        cy.jira_login('admin', 'admin');
        cy.clearCookies();
        let authStr = Buffer.from("admin:admin").toString('base64');
        cy.request({method: 'DELETE',
                    url: reset,
                    headers: {"Content-Type": "application/json",
                              "X-Atlassian-Token": "no-check",
                              "Authorization": authStr},
                    body: {}});
        // cy.window().then((win: Window) => {
        //     win.
        // });
    });

    it('Can provision a cloudformation template', () => {
        cy.visit(home);

        cy.get('[data-test=start-button]')
            .should('exist')
            .click();

        // TODO: This should load automatically once the start button
        // progresses to the AWS auth page automatically.
        cy.visit(awsAuth);

    });

});

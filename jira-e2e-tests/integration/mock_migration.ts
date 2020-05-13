/// <reference types="Cypress" />
import * as jira from '../support';

describe('Database Migration page', () => {
    beforeEach(() => {
        cy.on('uncaught:exception', (err, runnable) => false);

        Cypress.on('window:before:load', (win) => {
            delete win.fetch
        })
        cy.server();

        cy.jira_login('admin', 'admin');
        cy.reset_migration();
    });

    it('End to end mock PoC', () => {
        cy.route({
            method: 'GET',
            url: '/jira/rest/dc-migration/1.0/migration/ready',
            response: {
                dbCompatible: true,
                osCompatible: true,
                fsSizeCompatible: true,
            },
        });
        cy.route({
            method: 'POST',
            url: '/jira/rest/dc-migration/1.0/migration',
            response: {},
        });

        // Home; should be no migration; start one
        cy.visit(jira.migrationHome);
        cy.get('[data-test=start-migration]')
            .should('exist')
            .click();


        // AWS auth page.
        cy.location().should((loc: Location) => {
            expect(loc.pathname).to.eq("/jira/plugins/servlet/dc-migration-assistant/aws/auth")
        });

        cy.route({
            method: 'POST',
            url: '/jira/rest/dc-migration/1.0/aws/configure',
            status: 204,
            response: {},
        });

        cy.get('[data-test=aws-auth-key]').type('AWS_KEY');
        cy.get('[data-test=aws-auth-secret]').type('AWS_SECRET');
        // FIXME: This may be flaky; the AtlasKit AsyncSelect
        // component is hard to instrument.
        cy.get('#region-uid3').click();
        cy.get(`[id^=react-select]:contains(ap-southeast-2)`).click();
        cy.get('[data-test=aws-auth-submit]').click();


        // Quickstart page; bare minimum config
        // Note: These names are generated from the QS yaml
        cy.route({
            method: 'GET',
            url: '/jira/rest/dc-migration/1.0/aws/availabilityZones',
            response: ["ap-southeast-2a","ap-southeast-2b","ap-southeast-2c"]
        });
        cy.route({
            method: 'POST',
            url: '/jira/rest/dc-migration/1.0/aws/stack/create',
            status: 202,
            response: {},
        });

        cy.get('[name=stackName]').type('teststack');
        cy.get('[name=DBMasterUserPassword]').type('LKJLKJLlkjlkjl7987987#');
        cy.get('[name=DBPassword]').type('LKJLKJLlkjlkjl7987987#');
        cy.get('[name=DBMultiAZ]').type('false', {force: true});
        cy.get('[name=AccessCIDR]').type('0.0.0.0/0');
        cy.get('[name=KeyPairName]').type('taskcat-ci-key');
        cy.get('#AvailabilityZones-uid28').click();
        cy.get('#react-select-11-option-0').click();
        cy.get('#AvailabilityZones-uid28').click();
        cy.get('#react-select-11-option-1').click();
        cy.get('[name=ExportPrefix]').type('TEST-VPC-');
        cy.get('[data-test=qs-submit]').click();

        cy.route({
            method: 'GET',
            url: '/jira/rest/dc-migration/1.0/migration',
            response: {"stage":"provision_application_wait"}
        });

        cy.route({
            method: 'GET',
            url: '/jira/rest/dc-migration/1.0/aws/stack/status',
            response: {"status":{"state":"CREATE_IN_PROGRESS","reason":"User Initiated"},"phase":"app_infra"}
        }).as('status1');
        cy.wait('@status1');

        cy.route({
            method: 'GET',
            url: '/jira/rest/dc-migration/1.0/aws/stack/status',
            response: {"status":{"state":"CREATE_COMPLETE","reason":"User Initiated"},"phase":"app_infra"}
        }).as('status2');
        cy.wait('@status2');

    });

});

export const startMigration = (ctx: Context) => {
    cy.visit(ctx.migrationHome);
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.context + '/plugins/servlet/dc-migration-assistant/home');
    });
    cy.get('[data-test=start-migration]').should('exist').click();
};

export const fillCrendetialsOnAuthPage = (
    ctx: Context,
    region: string,
    credentials: AWSCredentials
) => {
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
};

export const selectPrefixOnASIPage = (ctx: Context, prefix: string = 'ATL-') => {
    cy.location().should((loc: Location) => {
        expect(loc.pathname).to.eq(ctx.context + '/plugins/servlet/dc-migration-assistant/aws/asi');
    });

    cy.get('section').contains("We're scanning your AWS account for existing ASIs.");

    cy.get('section').contains('We found an existing ASI', { timeout: 20000 });
    cy.get('[name=deploymentMode]').check('existing');
    cy.get('.asi-select')
        .click()
        .then(() => {
            cy.get(`[id^=react-select]:contains(${prefix})`).click();
        });

    cy.get('[data-test=asi-submit]').contains('Next').click();
};

export const configureQuickStartFormWithoutVPC = (
    ctx: Context,
    values: CloudFormationFormValues
) => {
    cy.get('[name=stackName]').type(values.stackName);
    cy.get('[name=DBMasterUserPassword]').type('LKJLKJLlkjlkjl7987987#');
    cy.get('[name=DBPassword]').type('LKJLKJLlkjlkjl7987987#');
    cy.get('[name=DBMultiAZ]').type('false', { force: true });
    cy.get('[name=CidrBlock]').type('0.0.0.0/0');
};

export const submitQuickstartForm = () => {
    cy.get('[data-test=qs-submit]').contains('Deploy').click();
};

/// <reference types="Cypress" />


export const baseURL = 'http://localhost:2990/jira';
export const welcomeURL = baseURL+'/secure/WelcomeToJIRA.jspa'
export const loginURL = baseURL+'/login.jsp';

Cypress.Commands.add('jira_login', (uname, passwd) => {
    cy.visit(loginURL);

    cy.get('#login-form-username').type('admin');
    cy.get('#login-form-password').type('admin');
    cy.get('#login-form-submit').click();
})


Cypress.Commands.add('jira_setup', () => {
    // Language
    cy.get("#next").click();

    // Avatar
    cy.get("avatar-picker-done").click();

    // Create sample project
    cy.get("#sampleData").click();
    cy.get("create-project-dialog-create-button").click();
    cy.get("#next").type("Test");
    cy.get("add-project-dialog-create-button").click();
};

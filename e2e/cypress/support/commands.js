Cypress.Commands.add('loginAdmin', () => {
    cy.fixture('settings').then(settings => {
        cy.visit(settings.baseUrl);
        cy.get('a[routerlink="/login"]').click();
        cy.get('input[name="username"]').type(settings.adminUser);
        cy.get('input[name="password"]').type(settings.adminPw);
        cy.contains('button', 'Login').click();
    })
})

Cypress.Commands.add('createMessage', (msg) => {
    cy.fixture('settings').then(settings => {
        cy.contains('button', 'Add message').click();
        cy.get('input[name="title"]', { timeout: 500 }).should('be.visible').type('title' + msg);
        cy.get('textarea[name="summary"]').type('summary' + msg);
        cy.get('textarea[name="text"]').type('text' + msg);
        cy.get('button[id="add-msg"]').click();
        cy.wait(500);
        cy.get('button[id="close-modal-btn"]').click();
        
        cy.contains('title' + msg).should('be.visible');
        cy.contains('summary' + msg).should('be.visible');
    })
})

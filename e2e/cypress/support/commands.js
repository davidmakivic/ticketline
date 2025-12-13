Cypress.Commands.add('loginAdmin', () => {
    cy.fixture('settings').then(settings => {
        cy.visit('http://' + settings.baseUrl);
        cy.get('a[routerlink="/login"]').click();
        cy.get('input[type="email"]').type(settings.adminUser);
        cy.get('input[type="password"]').type(settings.adminPw);
        cy.contains('button', 'Login').click();
        cy.wait(1000);
    })
})

Cypress.Commands.add('createMessage', (msg) => {
    cy.fixture('settings').then(settings => {
        cy.contains('button', 'Add message').should('exist');
        cy.contains('button', 'Add message').click();
        cy.get('input[name="title"]', { timeout: 5000 }).should('be.visible').type('title' + msg);
        cy.get('textarea[name="summary"]').should('be.visible').type('summary' + msg);
        cy.get('textarea[name="text"]').should('be.visible').type('text' + msg);
        cy.get('button[id="add-msg"]').click();
        cy.wait(1000);
        cy.get('button[id="close-modal-btn"]').click();
        
        cy.contains('title' + msg).should('be.visible');
        cy.contains('summary' + msg).should('be.visible');
    })
})
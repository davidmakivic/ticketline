context('add message', () => {
    let msgText = 'msg' + new Date().getTime();
    
    it('create message', () => {
        cy.loginAdmin();
        cy.visit('http://localhost:4200/#/news');
        cy.wait(1000);
        cy.createMessage(msgText);
    })
    
});

package ca.ulaval.glo4003.repul.fixture.user;

import ca.ulaval.glo4003.repul.user.api.request.AddCardRequest;

public class AddCardRequestFixture {
    private String cardNumber = "123456789";

    public AddCardRequestFixture withCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public AddCardRequest build() {
        AddCardRequest addCardRequest = new AddCardRequest();
        addCardRequest.cardNumber = cardNumber;

        return addCardRequest;
    }
}

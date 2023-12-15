package ca.ulaval.glo4003.repul.fixture.lockerauthorization;

import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;

public class OpenLockerRequestFixture {
    private String subscriberCardNumber = "123456789";
    private String lockerId = "3";

    public OpenLockerRequestFixture withSubscriberCardNumber(String subscriberCardNumber) {
        this.subscriberCardNumber = subscriberCardNumber;
        return this;
    }

    public OpenLockerRequestFixture withLockerId(String lockerId) {
        this.lockerId = lockerId;
        return this;
    }

    public OpenLockerRequest build() {
        OpenLockerRequest openLockerRequest = new OpenLockerRequest();
        openLockerRequest.subscriberCardNumber = subscriberCardNumber;
        openLockerRequest.lockerId = lockerId;
        return openLockerRequest;
    }
}

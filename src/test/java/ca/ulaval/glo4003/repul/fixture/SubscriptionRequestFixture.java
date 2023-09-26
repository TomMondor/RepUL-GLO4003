package ca.ulaval.glo4003.repul.fixture;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;

public class SubscriptionRequestFixture {
    private String locationId = "VACHON";
    private String dayOfWeek = DayOfWeek.MONDAY.toString();

    public SubscriptionRequestFixture withLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public SubscriptionRequestFixture withDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public SubscriptionRequest build() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.locationId = locationId;
        subscriptionRequest.dayOfWeek = dayOfWeek;
        return subscriptionRequest;
    }
}

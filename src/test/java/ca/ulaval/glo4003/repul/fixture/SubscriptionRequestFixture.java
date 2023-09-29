package ca.ulaval.glo4003.repul.fixture;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;

public class SubscriptionRequestFixture {
    private String locationId = "VACHON";
    private String dayOfWeek = DayOfWeek.MONDAY.toString();
    private String lunchboxType = LunchboxType.STANDARD.toString();

    public SubscriptionRequestFixture withLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public SubscriptionRequestFixture withDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public SubscriptionRequestFixture withLunchboxType() {
        this.lunchboxType = lunchboxType;
        return this;
    }

    public SubscriptionRequest build() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.locationId = locationId;
        subscriptionRequest.dayOfWeek = dayOfWeek;
        subscriptionRequest.lunchboxType = lunchboxType;
        return subscriptionRequest;
    }
}

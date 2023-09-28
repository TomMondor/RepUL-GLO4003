package ca.ulaval.glo4003.repul.application.subscription.query;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public record SubscriptionQuery(LocationId locationId, DayOfWeek dayOfWeek) {
    public SubscriptionQuery(String locationId, String dayOfWeek) {
        this(new LocationId(locationId), DayOfWeek.valueOf(dayOfWeek));
    }

    public static SubscriptionQuery from(String locationId, String dayOfWeek) {
        return new SubscriptionQuery(locationId, dayOfWeek);
    }
}

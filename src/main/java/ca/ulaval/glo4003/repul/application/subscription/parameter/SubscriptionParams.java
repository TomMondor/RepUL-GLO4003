package ca.ulaval.glo4003.repul.application.subscription.parameter;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public record SubscriptionParams(LocationId locationId, DayOfWeek dayOfWeek) {
    public SubscriptionParams(String locationId, String dayOfWeek) {
        this(new LocationId(locationId), DayOfWeek.valueOf(dayOfWeek));
    }

    public static SubscriptionParams from(String locationId, String dayOfWeek) {
        return new SubscriptionParams(locationId, dayOfWeek);
    }
}

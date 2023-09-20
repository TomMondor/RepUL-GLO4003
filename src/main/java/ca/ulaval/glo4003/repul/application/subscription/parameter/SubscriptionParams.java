package ca.ulaval.glo4003.repul.application.subscription.parameter;

import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public record SubscriptionParams(LocationId locationId) {
    public SubscriptionParams(String locationId) {
        this(new LocationId(locationId));
    }

    public static SubscriptionParams from(String locationId) {
        return new SubscriptionParams(locationId);
    }
}

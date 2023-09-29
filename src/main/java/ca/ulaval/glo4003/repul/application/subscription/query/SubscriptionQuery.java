package ca.ulaval.glo4003.repul.application.subscription.query;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public record SubscriptionQuery(LocationId locationId, DayOfWeek dayOfWeek, LunchboxType lunchboxType) {
    public SubscriptionQuery(String locationId, String dayOfWeek, String lunchboxType) {
        this(new LocationId(locationId), DayOfWeek.valueOf(dayOfWeek), LunchboxType.valueOf(lunchboxType));
    }

    public static SubscriptionQuery from(String locationId, String dayOfWeek, String lunchboxType) {
        return new SubscriptionQuery(locationId, dayOfWeek, lunchboxType);
    }
}

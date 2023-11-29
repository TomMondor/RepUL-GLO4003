package ca.ulaval.glo4003.repul.subscription.application.query;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDayOfWeekException;

public record SubscriptionQuery(
    DeliveryLocationId deliveryLocationId,
    DayOfWeek dayOfWeek,
    MealKitType mealKitType
) {
    public SubscriptionQuery(String deliveryLocationId, String dayOfWeek, String mealKitType) {
        this(new DeliveryLocationId(deliveryLocationId), DayOfWeek.valueOf(dayOfWeek), MealKitType.from(mealKitType));
    }

    public static SubscriptionQuery from(String deliveryLocationId, String dayOfWeek, String mealKitType) {
        try {
            DayOfWeek.valueOf(dayOfWeek);
        } catch (IllegalArgumentException e) {
            throw new InvalidDayOfWeekException();
        }
        return new SubscriptionQuery(deliveryLocationId, dayOfWeek, mealKitType);
    }
}

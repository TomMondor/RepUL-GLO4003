package ca.ulaval.glo4003.repul.subscription.application.event;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public final class MealKitConfirmedEvent extends RepULEvent {
    public final UniqueIdentifier mealKitId;
    public final UniqueIdentifier subscriptionId;
    public final UniqueIdentifier accountId;
    public final MealKitType mealKitType;
    public final DeliveryLocationId deliveryLocationId;
    public final LocalDate deliveryDate;

    public MealKitConfirmedEvent(UniqueIdentifier mealKitId, UniqueIdentifier subscriptionId, UniqueIdentifier accountId,
                                 MealKitType mealKitType, DeliveryLocationId subscriberDeliveryLocationId, LocalDate deliveryDate
    ) {
        this.mealKitId = mealKitId;
        this.subscriptionId = subscriptionId;
        this.accountId = accountId;
        this.mealKitType = mealKitType;
        this.deliveryLocationId = subscriberDeliveryLocationId;
        this.deliveryDate = deliveryDate;
    }
}

package ca.ulaval.glo4003.repul.subscription.application.event;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;

public final class MealKitConfirmedEvent extends RepULEvent {
    public final MealKitUniqueIdentifier mealKitId;
    public final SubscriptionUniqueIdentifier subscriptionId;
    public final SubscriberUniqueIdentifier subscriberId;
    public final MealKitType mealKitType;
    public final DeliveryLocationId deliveryLocationId;
    public final LocalDate deliveryDate;

    public MealKitConfirmedEvent(MealKitUniqueIdentifier mealKitId, SubscriptionUniqueIdentifier subscriptionId, SubscriberUniqueIdentifier subscriberId,
                                 MealKitType mealKitType, DeliveryLocationId subscriberDeliveryLocationId, LocalDate deliveryDate) {
        super();
        this.mealKitId = mealKitId;
        this.subscriptionId = subscriptionId;
        this.subscriberId = subscriberId;
        this.mealKitType = mealKitType;
        this.deliveryLocationId = subscriberDeliveryLocationId;
        this.deliveryDate = deliveryDate;
    }
}

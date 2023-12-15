package ca.ulaval.glo4003.repul.delivery.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public class RecalledDeliveryEvent extends RepULEvent {
    public final MealKitDto mealKitDto;
    public final LockerId lockerId;
    public final DeliveryLocationId deliveryLocationId;

    public RecalledDeliveryEvent(MealKitDto mealKitDto, LockerId lockerId, DeliveryLocationId deliveryLocationId) {
        super();
        this.mealKitDto = mealKitDto;
        this.lockerId = lockerId;
        this.deliveryLocationId = deliveryLocationId;
    }
}

package ca.ulaval.glo4003.repul.delivery.application.event;

import java.time.LocalTime;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public class ConfirmedDeliveryEvent extends RepULEvent {
    public UniqueIdentifier mealKitId;
    public DeliveryLocationId deliveryLocationId;
    public Optional<LockerId> lockerId;
    public LocalTime deliveryTime;

    public ConfirmedDeliveryEvent(UniqueIdentifier mealKitId, DeliveryLocationId deliveryLocationId, Optional<LockerId> lockerId, LocalTime deliveryTime) {
        super();
        this.mealKitId = mealKitId;
        this.deliveryLocationId = deliveryLocationId;
        this.lockerId = lockerId;
        this.deliveryTime = deliveryTime;
    }
}

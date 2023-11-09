package ca.ulaval.glo4003.repul.delivery.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class RecalledDeliveryEvent extends RepULEvent {
    public final UniqueIdentifier mealKitId;

    public RecalledDeliveryEvent(UniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
    }

}

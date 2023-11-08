package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class PickedUpCargoEvent extends RepULEvent {
    public final List<UniqueIdentifier> mealKitIds;

    public PickedUpCargoEvent(List<UniqueIdentifier> mealKitIds) {
        super();
        this.mealKitIds = mealKitIds;
    }
}

package ca.ulaval.glo4003.repul.shipping.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitReceivedForDeliveryEvent extends RepULEvent {
    public final KitchenLocationId kitchenLocationId;
    public final UniqueIdentifier ticketId;
    public final List<UniqueIdentifier> availableShippers;
    public final List<MealKitDeliveryInfoDto> mealKitDeliveryInfos;

    public MealKitReceivedForDeliveryEvent(UniqueIdentifier ticketId, KitchenLocationId kitchenLocationId, List<UniqueIdentifier> availableShippers,
                                           List<MealKitDeliveryInfoDto> mealKitDeliveryInfos) {
        this.ticketId = ticketId;
        this.availableShippers = availableShippers;
        this.kitchenLocationId = kitchenLocationId;
        this.mealKitDeliveryInfos = mealKitDeliveryInfos;
    }
}

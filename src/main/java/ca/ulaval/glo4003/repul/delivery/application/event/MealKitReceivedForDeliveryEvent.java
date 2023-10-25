package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitReceivedForDeliveryEvent extends RepULEvent {
    public final KitchenLocationId kitchenLocationId;
    public final UniqueIdentifier cargoId;
    public final List<UniqueIdentifier> availableDeliveryPeople;
    public final List<MealKitDto> mealKitDtos;

    public MealKitReceivedForDeliveryEvent(UniqueIdentifier cargoId, KitchenLocationId kitchenLocationId, List<UniqueIdentifier> availableDeliveryPeople,
                                           List<MealKitDto> mealKitDtos) {
        this.cargoId = cargoId;
        this.availableDeliveryPeople = availableDeliveryPeople;
        this.kitchenLocationId = kitchenLocationId;
        this.mealKitDtos = mealKitDtos;
    }
}

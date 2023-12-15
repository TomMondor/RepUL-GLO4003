package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public record MealKitToDeliverDto(DeliveryLocationId deliveryLocationId, Optional<LockerId> lockerId, MealKitUniqueIdentifier mealKitId) {
}


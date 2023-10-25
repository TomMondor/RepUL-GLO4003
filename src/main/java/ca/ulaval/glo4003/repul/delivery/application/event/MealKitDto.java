package ca.ulaval.glo4003.repul.delivery.application.event;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public record MealKitDto(DeliveryLocationId deliveryLocationId, LockerId lockerId, UniqueIdentifier mealKitId) {
}


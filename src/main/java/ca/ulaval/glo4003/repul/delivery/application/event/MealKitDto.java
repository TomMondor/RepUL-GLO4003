package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public record MealKitDto(DeliveryLocationId deliveryLocationId, Optional<LockerId> lockerId, UniqueIdentifier mealKitId) {
}


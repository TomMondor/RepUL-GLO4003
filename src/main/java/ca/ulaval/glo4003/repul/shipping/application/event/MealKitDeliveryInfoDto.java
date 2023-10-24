package ca.ulaval.glo4003.repul.shipping.application.event;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;

public record MealKitDeliveryInfoDto(DeliveryLocationId shippingLocationId, LockerId lockerId, UniqueIdentifier mealkitId) {
}

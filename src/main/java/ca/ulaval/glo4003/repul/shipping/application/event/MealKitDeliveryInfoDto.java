package ca.ulaval.glo4003.repul.shipping.application.event;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public record MealKitDeliveryInfoDto(DeliveryLocationId shippingLocationId, CaseId caseId, UniqueIdentifier mealkitId) {
}

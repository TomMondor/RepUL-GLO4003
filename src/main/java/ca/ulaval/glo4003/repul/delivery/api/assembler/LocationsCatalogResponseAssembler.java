package ca.ulaval.glo4003.repul.delivery.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.api.response.DeliveryLocationResponse;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;

public class LocationsCatalogResponseAssembler {
    public List<DeliveryLocationResponse> toLocationsResponse(DeliveryLocationsPayload deliveryLocationsPayload) {
        return deliveryLocationsPayload.deliveryLocations().stream().map(
            deliveryLocation -> new DeliveryLocationResponse(deliveryLocation.deliveryLocationId().toString(), deliveryLocation.name(),
                deliveryLocation.remainingCapacity())).toList();
    }
}

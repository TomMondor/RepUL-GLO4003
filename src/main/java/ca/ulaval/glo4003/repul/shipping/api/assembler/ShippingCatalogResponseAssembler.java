package ca.ulaval.glo4003.repul.shipping.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.shipping.api.response.LocationResponse;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationsPayload;

public class ShippingCatalogResponseAssembler {
    public List<LocationResponse> toLocationsResponse(LocationsPayload locationsPayload) {
        return locationsPayload.shippingLocations().stream()
            .map(pickupLocation -> new LocationResponse(pickupLocation.deliveryLocationId().value(), pickupLocation.name(), pickupLocation.remainingCapacity()))
            .toList();
    }
}

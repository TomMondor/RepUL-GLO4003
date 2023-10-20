package ca.ulaval.glo4003.shipping.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.shipping.api.response.LocationResponse;
import ca.ulaval.glo4003.shipping.application.payload.LocationsPayload;

public class ShippingCatalogResponseAssembler {
    public List<LocationResponse> toLocationsResponse(LocationsPayload locationsPayload) {
        return locationsPayload.shippingLocations().stream().map(
            pickupLocation -> new LocationResponse(
                pickupLocation.locationId().value(),
                pickupLocation.name(),
                pickupLocation.remainingCapacity())).toList();
    }
}

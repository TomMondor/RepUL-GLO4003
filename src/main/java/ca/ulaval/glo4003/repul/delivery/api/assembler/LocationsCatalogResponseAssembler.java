package ca.ulaval.glo4003.repul.delivery.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.api.response.LocationResponse;
import ca.ulaval.glo4003.repul.delivery.application.payload.LocationsPayload;

public class LocationsCatalogResponseAssembler {
    public List<LocationResponse> toLocationsResponse(LocationsPayload locationsPayload) {
        return locationsPayload.deliveryLocations().stream().map(
            deliveryLocation -> new LocationResponse(deliveryLocation.deliveryLocationId().value(), deliveryLocation.name(),
                deliveryLocation.remainingCapacity())).toList();
    }
}

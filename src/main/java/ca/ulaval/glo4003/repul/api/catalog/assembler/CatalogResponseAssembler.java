package ca.ulaval.glo4003.repul.api.catalog.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.api.catalog.response.LocationResponse;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationsPayload;

public class CatalogResponseAssembler {
    public List<LocationResponse> toLocationsResponse(LocationsPayload locationsPayload) {
        return locationsPayload.pickupLocations().stream().map(
            pickupLocation -> new LocationResponse(
                pickupLocation.getLocationId().value(),
                pickupLocation.getName(),
                pickupLocation.getRemainingCapacity())).toList();
    }
}

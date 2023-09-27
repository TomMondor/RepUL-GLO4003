package ca.ulaval.glo4003.repul.application.catalog.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

public record LocationsPayload(List<PickupLocation> pickupLocations) {
}

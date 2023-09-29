package ca.ulaval.glo4003.repul.application.catalog.payload;

import java.util.List;

public record LocationsPayload(List<LocationPayload> pickupLocations) {
}

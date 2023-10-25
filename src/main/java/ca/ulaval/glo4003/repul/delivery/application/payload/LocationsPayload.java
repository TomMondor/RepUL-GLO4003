package ca.ulaval.glo4003.repul.delivery.application.payload;

import java.util.List;

public record LocationsPayload(List<LocationPayload> deliveryLocations) {
}

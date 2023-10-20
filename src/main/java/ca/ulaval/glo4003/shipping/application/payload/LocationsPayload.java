package ca.ulaval.glo4003.shipping.application.payload;

import java.util.List;

public record LocationsPayload(List<LocationPayload> shippingLocations) {
}

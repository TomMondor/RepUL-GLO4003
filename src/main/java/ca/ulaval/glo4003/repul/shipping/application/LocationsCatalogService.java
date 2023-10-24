package ca.ulaval.glo4003.repul.shipping.application;

import ca.ulaval.glo4003.repul.shipping.application.exception.ShippingNotFoundException;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;

public class LocationsCatalogService {
    private final ShippingRepository shippingRepository;

    public LocationsCatalogService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    public LocationsPayload getShippingLocations() {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        return new LocationsPayload(shipping.getShippingLocations().stream().map(
            shippingLocation -> new LocationPayload(shippingLocation.getLocationId(), shippingLocation.getName(), shippingLocation.getTotalCapacity(),
                shippingLocation.getRemainingCapacity())).toList());
    }
}

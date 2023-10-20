package ca.ulaval.glo4003.shipping.application;

import ca.ulaval.glo4003.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.shipping.domain.Shipping;
import ca.ulaval.glo4003.shipping.domain.ShippingRepository;
import ca.ulaval.glo4003.shipping.domain.exception.ShippingNotFoundException;

public class ShippingCatalogService {
    private final ShippingRepository shippingRepository;

    public ShippingCatalogService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    public LocationsPayload getShippingLocations() {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        return new LocationsPayload(shipping.getShippingLocations().stream().map(
            shippingLocation -> new LocationPayload(shippingLocation.getLocationId(), shippingLocation.getName(), shippingLocation.getTotalCapacity(),
                shippingLocation.getRemainingCapacity())).toList());
    }
}

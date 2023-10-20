package ca.ulaval.glo4003.shipping.domain.commons;

import ca.ulaval.glo4003.commons.domain.LocationId;

public class PickupLocation {
    private final LocationId locationId;
    private final String name;

    public PickupLocation(LocationId locationId, String name) {
        this.locationId = locationId;
        this.name = name;
    }

    public LocationId getLocationId() {
        return locationId;
    }
}

package ca.ulaval.glo4003.shipping.domain.commons;

import ca.ulaval.glo4003.commons.domain.LocationId;

public class ShippingLocation {
    private final LocationId locationId;
    private final String name;
    private final int totalCapacity;
    private final int remainingCapacity;

    public ShippingLocation(LocationId locationId, String name, int totalCapacity) {
        this.locationId = locationId;
        this.name = name;
        this.totalCapacity = totalCapacity;
        this.remainingCapacity = totalCapacity;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }

    public int getRemainingCapacity() {
        return remainingCapacity;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }
}

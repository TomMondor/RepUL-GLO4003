package ca.ulaval.glo4003.repul.domain.catalog;

public class PickupLocation {
    private final LocationId locationId;
    private final String name;
    private final int totalCapacity;
    private final int remainingCapacity;

    public PickupLocation(LocationId locationId, String name, int totalCapacity) {
        this.locationId = locationId;
        this.name = name;
        this.totalCapacity = totalCapacity;
        this.remainingCapacity = totalCapacity;
    }

    public LocationId getLocationId() {
        return locationId;
    }
}

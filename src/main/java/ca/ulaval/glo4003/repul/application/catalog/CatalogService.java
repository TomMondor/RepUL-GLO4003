package ca.ulaval.glo4003.repul.application.catalog;

import ca.ulaval.glo4003.repul.application.catalog.payload.LocationPayload;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;

public class CatalogService {
    private final RepULRepository repULRepository;

    public CatalogService(RepULRepository repULRepository) {
        this.repULRepository = repULRepository;
    }

    public LocationsPayload getPickupLocations() {
        RepUL repUL = repULRepository.get();

        return new LocationsPayload(repUL.getPickupLocations().stream().map(
            pickupLocation -> new LocationPayload(pickupLocation.getLocationId(), pickupLocation.getName(), pickupLocation.getTotalCapacity(),
                pickupLocation.getRemainingCapacity())).toList());
    }
}

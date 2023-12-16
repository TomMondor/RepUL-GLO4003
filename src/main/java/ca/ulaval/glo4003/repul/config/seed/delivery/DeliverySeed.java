package ca.ulaval.glo4003.repul.config.seed.delivery;

import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;

public abstract class DeliverySeed implements Seed {
    protected final DeliverySystemPersister deliverySystemPersister;
    protected final LocationsCatalog locationsCatalog;

    protected DeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog) {
        this.deliverySystemPersister = deliverySystemPersister;
        this.locationsCatalog = locationsCatalog;
    }
}

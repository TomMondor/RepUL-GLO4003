package ca.ulaval.glo4003.repul.config.seed.delivery;

import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;

public class DevDeliverySeed extends DeliverySeed {
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");

    public DevDeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog) {
        super(deliverySystemPersister, locationsCatalog);
    }

    @Override
    public void populate() {
        DeliverySystem deliverySystem = new DeliverySystem(locationsCatalog);
        createDeliveryPerson(deliverySystem);
        deliverySystemPersister.save(deliverySystem);
    }

    private void createDeliveryPerson(DeliverySystem deliverySystem) {
        deliverySystem.addDeliveryPerson(DELIVERY_PERSON_ID);
    }
}

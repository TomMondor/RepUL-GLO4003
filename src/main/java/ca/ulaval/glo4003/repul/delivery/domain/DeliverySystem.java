package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKitFactory;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;

public class DeliverySystem {
    private final Map<UniqueIdentifier, MealKit> pendingMealKits = new HashMap<>();
    private final List<Cargo> cargos = new ArrayList<>();
    private final LocationsCatalog locationsCatalog;
    private final CargoFactory cargoFactory;
    private final MealKitFactory mealKitFactory;
    private final List<UniqueIdentifier> deliveryPersonIds = new ArrayList<>();

    public DeliverySystem(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.cargoFactory = new CargoFactory();
        this.mealKitFactory = new MealKitFactory();
    }

    public void createMealKit(DeliveryLocationId deliveryLocationId, UniqueIdentifier mealKitId) {
        pendingMealKits.put(mealKitId, mealKitFactory.createMealKit(locationsCatalog.getDeliveryLocation(deliveryLocationId), mealKitId));
    }

    // TODO : Regarder si on garde vraiment cette fonction qui est seulement utilisé dans les tests
    public List<Cargo> getCargos() {
        return cargos;
    }

    public Cargo receiveReadyToBeDeliveredMealKit(KitchenLocationId kitchenLocationId, List<UniqueIdentifier> mealKitIds) {
        List<MealKit> mealKits = mealKitIds.stream().map(this.pendingMealKits::remove).toList();

        mealKits.forEach(MealKit::assignLocker);

        mealKits.forEach(MealKit::markAsReadyToBeDelivered);

        Cargo cargo = cargoFactory.createCargo(locationsCatalog.getKitchenLocation(kitchenLocationId), mealKits);

        cargos.add(cargo);

        return cargo;
    }

    public MealKit getMealKit(UniqueIdentifier mealKitId) {
        return pendingMealKits.get(mealKitId);
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return locationsCatalog.getDeliveryLocations();
    }

    public List<MealKit> pickupCargo(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId) {
        validateDeliveryPersonExists(deliveryPersonId);
        return cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .pickupCargo(deliveryPersonId);
    }

    private void validateDeliveryPersonExists(UniqueIdentifier deliveryPersonId) {
        if (!deliveryPersonIds.contains(deliveryPersonId)) {
            throw new DeliveryPersonNotFoundException();
        }
    }

    public void cancelCargo(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId) {
        cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .cancelCargo(deliveryPersonId);
    }

    public void confirmDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .confirmDelivery(deliveryPersonId, mealKitId);
    }

    public LockerId recallDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        return cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .recallDelivery(deliveryPersonId, mealKitId);
    }

    public void addDeliveryPerson(UniqueIdentifier deliveryPersonId) {
        deliveryPersonIds.add(deliveryPersonId);
    }

    public List<UniqueIdentifier> getDeliveryPeople() {
        return deliveryPersonIds;
    }
}

package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.MealKitFactory;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShippingIdException;

public class Shipping {
    private final Map<UniqueIdentifier, MealKit> pendingMealKits = new HashMap<>();
    private final List<Cargo> cargos = new ArrayList<>();
    private final LocationsCatalog locationsCatalog;
    private final CargoFactory cargoFactory;
    private final MealKitFactory mealKitFactory;
    private final List<UniqueIdentifier> deliveryAccountIds = new ArrayList<>();

    public Shipping(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.cargoFactory = new CargoFactory();
        this.mealKitFactory = new MealKitFactory();
    }

    public void createMealKit(DeliveryLocationId deliveryLocationId, UniqueIdentifier mealKitId) {
        pendingMealKits.put(mealKitId,
            mealKitFactory.createMealKit(locationsCatalog.getDeliveryLocation(deliveryLocationId), mealKitId));
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public Cargo receiveReadyToBeDeliveredMealKit(KitchenLocationId kitchenLocationId, List<UniqueIdentifier> mealKitIds) {
        List<MealKit> mealKits = mealKitIds.stream()
            .map(this.pendingMealKits::remove)
            .toList();

        mealKits.forEach(MealKit::assignLocker);

        mealKits.forEach(MealKit::markAsReadyToBeDelivered);

        Cargo cargo = cargoFactory.createCargo(
            locationsCatalog.getKitchenLocation(kitchenLocationId),
            mealKits
        );

        cargos.add(cargo);

        return cargo;
    }

    public MealKit getMealKit(UniqueIdentifier mealKitId) {
        return pendingMealKits.get(mealKitId);
    }

    public List<DeliveryLocation> getShippingLocations() {
        return locationsCatalog.getDeliveryLocations();
    }

    public List<MealKit> pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        validateDeliveryPersonExists(accountId);
        return cargos.stream()
            .filter(cargo -> cargo.getCargoId().equals(shippingId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .pickupCargo(accountId);
    }

    private void validateDeliveryPersonExists(UniqueIdentifier accountId) {
        if (!deliveryAccountIds.contains(accountId)) {
            throw new DeliveryPersonNotFoundException();
        }
    }

    public void cancelShipping(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        cargos.stream()
            .filter(cargo -> cargo.getCargoId().equals(shippingId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .cancelShipping(accountId);
    }

    public void confirmShipping(UniqueIdentifier accountId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        cargos.stream()
            .filter(cargo -> cargo.getCargoId().equals(cargoId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .confirmShipping(accountId, mealKitId);
    }

    public LockerId unconfirmShipping(UniqueIdentifier accountId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        return cargos.stream()
            .filter(cargo -> cargo.getCargoId().equals(cargoId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .unconfirmShipping(accountId, mealKitId);
    }

    public void addDeliveryPerson(UniqueIdentifier accountId) {
        deliveryAccountIds.add(accountId);
    }

    public List<UniqueIdentifier> getDeliveryPeople() {
        return deliveryAccountIds;
    }
}

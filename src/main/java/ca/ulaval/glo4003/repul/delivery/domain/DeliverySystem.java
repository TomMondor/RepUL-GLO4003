package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKitFactory;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;

public class DeliverySystem {
    private final Map<UniqueIdentifier, MealKit> pendingMealKits = new HashMap<>();
    private final List<Cargo> cargos = new ArrayList<>();
    private final LocationsCatalog locationsCatalog;
    private final CargoFactory cargoFactory;
    private final MealKitFactory mealKitFactory;
    private final List<UniqueIdentifier> deliveryPersonIds = new ArrayList<>();
    private final LockerAssignator lockerAssignator;

    public DeliverySystem(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.cargoFactory = new CargoFactory();
        this.mealKitFactory = new MealKitFactory();
        this.lockerAssignator = new LockerAssignator(locationsCatalog.getDeliveryLocations());
    }

    public void createMealKit(DeliveryLocationId deliveryLocationId, UniqueIdentifier mealKitId) {
        pendingMealKits.put(mealKitId, mealKitFactory.createMealKit(locationsCatalog.getDeliveryLocation(deliveryLocationId), mealKitId));
    }

    public List<Cargo> getCargosReadyToPickUp() {
        return cargos.stream().filter(Cargo::isReadyToBeDelivered).toList();
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public Cargo receiveReadyToBeDeliveredMealKits(KitchenLocationId kitchenLocationId, List<UniqueIdentifier> mealKitIds) {
        mealKitIds.stream().forEach(mealKitId -> {
            if (!pendingMealKits.containsKey(mealKitId)) {
                throw new InvalidMealKitIdException();
            }
        });

        List<MealKit> mealKits = mealKitIds.stream().map(this.pendingMealKits::remove).collect(Collectors.toCollection(ArrayList::new));

        mealKits.forEach(lockerAssignator::assignLocker);

        mealKits.forEach(MealKit::markAsReadyToBeDelivered);

        Cargo cargo = cargoFactory.createCargo(locationsCatalog.getKitchenLocation(kitchenLocationId), mealKits);

        cargos.add(cargo);

        return cargo;
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

    public List<MealKit> cancelCargo(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId) {
        return cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .cancelCargo(deliveryPersonId);
    }

    public void confirmDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .confirmDelivery(deliveryPersonId, mealKitId);
    }

    public MealKit recallDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        return cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new)
            .recallDelivery(deliveryPersonId, mealKitId);
    }

    public void addDeliveryPerson(UniqueIdentifier deliveryPersonId) {
        deliveryPersonIds.add(deliveryPersonId);
    }

    public List<UniqueIdentifier> getDeliveryPeople() {
        return deliveryPersonIds;
    }

    public void moveMealKitFromCargosToPending(UniqueIdentifier mealKitId) {
        MealKit removedMealKitFromCargos = removeMealKitFromCargos(mealKitId);
        pendingMealKits.put(mealKitId, removedMealKitFromCargos);
    }

    private MealKit removeMealKitFromCargos(UniqueIdentifier mealKitId) {
        for (Cargo cargo : cargos) {
            if (cargo.containsMealKit(mealKitId)) {
                MealKit mealKit = cargo.removeMealKit(mealKitId);
                if (cargo.isEmpty()) {
                    cargos.remove(cargo);
                }
                return mealKit;
            }
        }
        throw new InvalidMealKitIdException();
    }

    public MealKit getCargoMealKit(UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        return cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new).getMealKit(mealKitId);
    }

    public void removeMealKitFromLocker(UniqueIdentifier mealKitId) {
        Cargo cargo = cargos.stream().filter(c -> c.containsMealKit(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new);
        MealKit mealKit = cargo.getMealKit(mealKitId);
        lockerAssignator.unassignLocker(mealKit);
        cargo.removeMealKit(mealKitId);
    }
}

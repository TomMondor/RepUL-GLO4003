package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.CargoFactory;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargos;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocations;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKitFactory;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKits;

public class DeliverySystem {
    private final MealKits pendingMealKits;
    private final Cargos cargos;
    private final LocationsCatalog locationsCatalog;
    private final CargoFactory cargoFactory;
    private final MealKitFactory mealKitFactory;
    private final List<DeliveryPersonUniqueIdentifier> deliveryPersonIds = new ArrayList<>();
    private final DeliveryLocations deliveryLocations;

    public DeliverySystem(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.cargoFactory = new CargoFactory();
        this.mealKitFactory = new MealKitFactory();
        this.cargos = new Cargos();
        this.pendingMealKits = new MealKits();
        this.deliveryLocations = new DeliveryLocations(locationsCatalog.getDeliveryLocations());
    }

    public void createMealKitInPreparation(SubscriberUniqueIdentifier subscriberId,
                                           SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId,
                                           DeliveryLocationId deliveryLocationId) {
        MealKit createdMealKit = mealKitFactory.createMealKit(subscriberId, subscriptionId, mealKitId, deliveryLocationId);
        pendingMealKits.addMealKit(createdMealKit);
    }

    public List<Cargo> getCargosReadyToPickUp() {
        return cargos.findCargosReadyToPickUp();
    }

    public List<Cargo> getCargos() {
        return cargos.getAllCargos();
    }

    public Cargo receiveReadyToBeDeliveredMealKits(KitchenLocationId kitchenLocationId, List<MealKitUniqueIdentifier> mealKitIds) {
        List<MealKit> mealKits = pendingMealKits.removeMealKits(mealKitIds);

        deliveryLocations.assignLockers(mealKits);

        mealKits.forEach(MealKit::markAsReadyToBeDelivered);

        Cargo cargo = cargoFactory.createCargo(locationsCatalog.getKitchenLocation(kitchenLocationId), mealKits);

        cargos.add(cargo);

        return cargo;
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return deliveryLocations.getDeliveryLocations();
    }

    public List<MealKit> pickupCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        validateDeliveryPersonExists(deliveryPersonId);

        return cargos.pickupCargo(cargoId, deliveryPersonId);
    }

    private void validateDeliveryPersonExists(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (!deliveryPersonIds.contains(deliveryPersonId)) {
            throw new DeliveryPersonNotFoundException();
        }
    }

    public List<MealKit> cancelCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        validateDeliveryPersonExists(deliveryPersonId);

        return cargos.cancelPickedUpCargo(cargoId, deliveryPersonId);
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        validateDeliveryPersonExists(deliveryPersonId);

        cargos.confirmMealKitDelivery(deliveryPersonId, cargoId, mealKitId);
    }

    public MealKit recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        validateDeliveryPersonExists(deliveryPersonId);

        return cargos.recallMealKitDelivery(deliveryPersonId, cargoId, mealKitId);
    }

    public void addDeliveryPerson(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        deliveryPersonIds.add(deliveryPersonId);
    }

    public List<DeliveryPersonUniqueIdentifier> getDeliveryPeople() {
        return deliveryPersonIds;
    }

    public void moveMealKitFromCargosToPending(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKitRemovedFromCargo = cargos.extractMealKitFromCargo(mealKitId);
        deliveryLocations.unassignLocker(mealKitRemovedFromCargo);
        pendingMealKits.addMealKit(mealKitRemovedFromCargo);
    }

    public MealKit getCargoMealKit(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        return cargos.getMealKitFromCargo(cargoId, mealKitId);
    }

    public void removeMealKitFromLocker(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKitRemovedFromCargo = cargos.extractMealKitFromCargo(mealKitId);
        deliveryLocations.unassignLocker(mealKitRemovedFromCargo);
    }
}

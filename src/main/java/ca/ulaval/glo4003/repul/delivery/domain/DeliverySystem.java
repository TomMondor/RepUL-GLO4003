package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.CargoFactory;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargos;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocations;
import ca.ulaval.glo4003.repul.delivery.domain.deliveryperson.DeliveryPeople;
import ca.ulaval.glo4003.repul.delivery.domain.deliveryperson.DeliveryPerson;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKitFactory;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKits;

public class DeliverySystem {
    private final MealKits pendingMealKits;
    private final Cargos cargosWaitingToBePickedUp;
    private final LocationsCatalog locationsCatalog;
    private final CargoFactory cargoFactory;
    private final MealKitFactory mealKitFactory;
    private final DeliveryPeople deliveryPeople;
    private final DeliveryLocations deliveryLocations;

    public DeliverySystem(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.cargoFactory = new CargoFactory();
        this.deliveryPeople = new DeliveryPeople();
        this.mealKitFactory = new MealKitFactory();
        this.cargosWaitingToBePickedUp = new Cargos();
        this.pendingMealKits = new MealKits();
        this.deliveryLocations = new DeliveryLocations(locationsCatalog.getDeliveryLocations());
    }

    public void createPendingMealKit(SubscriberUniqueIdentifier subscriberId,
                                     SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId,
                                     DeliveryLocationId deliveryLocationId) {
        MealKit createdMealKit = mealKitFactory.createMealKit(subscriberId, subscriptionId, mealKitId, deliveryLocationId);
        pendingMealKits.addMealKit(createdMealKit);
    }

    public List<Cargo> getCargosReadyToPickUp() {
        return cargosWaitingToBePickedUp.findCargosReadyToPickUp();
    }

    public List<Cargo> getCargosInDelivery() {
        return deliveryPeople.getCargosInDelivery();
    }

    public Cargo receiveReadyToBeDeliveredMealKits(KitchenLocationId kitchenLocationId, List<MealKitUniqueIdentifier> mealKitIds) {
        List<MealKit> mealKits = pendingMealKits.removeMealKits(mealKitIds);

        deliveryLocations.assignLockers(mealKits);

        mealKits.forEach(MealKit::markAsReadyToBeDelivered);

        Cargo cargo = cargoFactory.createCargo(locationsCatalog.getKitchenLocation(kitchenLocationId), mealKits);

        cargosWaitingToBePickedUp.addCargoReadyToPickUp(cargo);

        return cargo;
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return deliveryLocations.getDeliveryLocations();
    }

    public List<MealKit> pickUpCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        DeliveryPerson deliveryPerson = deliveryPeople.findDeliveryPerson(deliveryPersonId);

        Cargo cargo = cargosWaitingToBePickedUp.pickUpCargo(cargoId);

        return deliveryPerson.pickUpCargo(cargo);
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliveryPerson deliveryPerson = deliveryPeople.findDeliveryPerson(deliveryPersonId);
        deliveryPerson.confirmMealKitDelivery(cargoId, mealKitId);
    }

    public MealKit recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliveryPerson deliveryPerson = deliveryPeople.findDeliveryPerson(deliveryPersonId);

        return deliveryPerson.recallMealKitDelivery(cargoId, mealKitId);
    }

    public void addDeliveryPerson(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        deliveryPeople.addDeliveryPerson(deliveryPersonId);
    }

    public List<DeliveryPersonUniqueIdentifier> getDeliveryPeopleIds() {
        return deliveryPeople.getDeliveryPeopleIds();
    }

    public void recallMealKitToPending(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKitRemovedFromCargo = cargosWaitingToBePickedUp.extractMealKitFromCargo(mealKitId);

        deliveryLocations.unassignLocker(mealKitRemovedFromCargo);

        pendingMealKits.addMealKit(mealKitRemovedFromCargo);
    }

    public MealKit findMealKit(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliveryPerson deliveryPerson = deliveryPeople.findDeliveryPersonThatHasCargoWithCargoId(cargoId);

        return deliveryPerson.findMealkitFromCargo(cargoId, mealKitId);
    }

    public void removeMealKitFromLocker(MealKitUniqueIdentifier mealKitId) {
        DeliveryPerson deliveryPerson = deliveryPeople.findDeliveryPersonThatHasMealKitWithMealKitId(mealKitId);

        MealKit mealKitRemovedFromCargo = deliveryPerson.removeMealKitFromCargo(mealKitId);

        deliveryLocations.unassignLocker(mealKitRemovedFromCargo);
    }
}

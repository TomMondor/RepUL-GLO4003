package ca.ulaval.glo4003.repul.delivery.application;

import java.time.LocalTime;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotAssignedException;

public class DeliveryService {
    private final DeliverySystemRepository deliverySystemRepository;
    private final RepULEventBus eventBus;

    public DeliveryService(DeliverySystemRepository deliverySystemRepository, RepULEventBus eventBus) {
        this.deliverySystemRepository = deliverySystemRepository;
        this.eventBus = eventBus;
    }

    public void createDeliveryPersonAccount(DeliveryPersonUniqueIdentifier accountId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.addDeliveryPerson(accountId);

        deliverySystemRepository.save(deliverySystem);
    }

    public void receiveMealKitForDelivery(DeliveryLocationId deliveryLocationId, MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.createMealKit(deliveryLocationId, mealKitId);

        deliverySystemRepository.save(deliverySystem);
    }

    public void createCargo(KitchenLocationId kitchenLocationId, List<MealKitUniqueIdentifier> mealKitIds) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        Cargo cargo = deliverySystem.receiveReadyToBeDeliveredMealKits(kitchenLocationId, mealKitIds);

        deliverySystemRepository.save(deliverySystem);

        sendMealKitReceivedForDeliveryEvent(cargo, deliverySystem);
    }

    public void recallMealKit(MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.moveMealKitFromCargosToPending(mealKitId);

        deliverySystemRepository.save(deliverySystem);
    }

    public void removeMealKitFromLocker(MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.removeMealKitFromLocker(mealKitId);

        deliverySystemRepository.save(deliverySystem);
    }

    private void sendMealKitReceivedForDeliveryEvent(Cargo cargo, DeliverySystem deliverySystem) {
        MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
            new MealKitReceivedForDeliveryEvent(cargo.getCargoId(), cargo.getKitchenLocation().getLocationId(), deliverySystem.getDeliveryPeople(),
                cargo.getMealKits().stream()
                    .map(mealKit -> new MealKitDto(mealKit.getDeliveryLocation().getLocationId(), mealKit.getLockerId(), mealKit.getMealKitId())).toList());
        eventBus.publish(mealKitReceivedForDeliveryEvent);
    }

    public CargosPayload getCargosReadyToPickUp() {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        return CargosPayload.from(deliverySystem.getCargosReadyToPickUp());
    }

    public void pickupCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        List<MealKit> mealKits = deliverySystem.pickupCargo(deliveryPersonId, cargoId);

        deliverySystemRepository.save(deliverySystem);

        sendPickedUpCargoEvent(mealKits);
    }

    private void sendPickedUpCargoEvent(List<MealKit> mealKits) {
        eventBus.publish(new PickedUpCargoEvent(mealKits.stream().map(MealKit::getMealKitId).toList()));
    }

    public void cancelCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        List<MealKit> mealKits = deliverySystem.cancelCargo(deliveryPersonId, cargoId);

        deliverySystemRepository.save(deliverySystem);

        eventBus.publish(new CanceledCargoEvent(mealKits.stream().map(MealKit::getMealKitId).toList()));
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.confirmDelivery(deliveryPersonId, cargoId, mealKitId);

        MealKit mealKit = deliverySystem.getCargoMealKit(cargoId, mealKitId);

        deliverySystemRepository.save(deliverySystem);

        eventBus.publish(
            new ConfirmedDeliveryEvent(mealKit.getMealKitId(), mealKit.getDeliveryLocation().getLocationId(), mealKit.getLockerId(), LocalTime.now()));
    }

    public LockerId recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        MealKit mealKit = deliverySystem.recallDelivery(deliveryPersonId, cargoId, mealKitId);
        LockerId lockerId = mealKit.getLockerId().orElseThrow(LockerNotAssignedException::new);

        deliverySystemRepository.save(deliverySystem);

        eventBus.publish(new RecalledDeliveryEvent(mealKitId, lockerId, mealKit.getDeliveryLocation().getLocationId()));

        return lockerId;
    }
}

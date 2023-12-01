package ca.ulaval.glo4003.repul.delivery.application;

import java.time.LocalTime;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
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
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class DeliveryService {
    private final DeliverySystemRepository deliverySystemRepository;
    private final RepULEventBus eventBus;

    public DeliveryService(DeliverySystemRepository deliverySystemRepository, RepULEventBus eventBus) {
        this.deliverySystemRepository = deliverySystemRepository;
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handleDeliveryPersonAccountCreatedEvent(DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.addDeliveryPerson(deliveryPersonAccountCreatedEvent.accountId);

        deliverySystemRepository.save(deliverySystem);
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.createMealKit(mealKitConfirmedEvent.deliveryLocationId, mealKitConfirmedEvent.mealKitId);

        deliverySystemRepository.save(deliverySystem);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent mealKitsCookedEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        KitchenLocationId kitchenLocationId = new KitchenLocationId(mealKitsCookedEvent.kitchenLocationId);

        Cargo cargo = deliverySystem.receiveReadyToBeDeliveredMealKits(kitchenLocationId, mealKitsCookedEvent.mealKitIds);

        deliverySystemRepository.save(deliverySystem);

        sendMealKitReceivedForDeliveryEvent(cargo, deliverySystem);
    }

    @Subscribe
    public void handleRecallCookedMealKitEvent(RecallCookedMealKitEvent recallCookedMealKitEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.moveMealKitFromCargosToPending(recallCookedMealKitEvent.mealKitId);

        deliverySystemRepository.save(deliverySystem);
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent mealKitPickedUpByUserEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get();

        deliverySystem.removeMealKitFromLocker(mealKitPickedUpByUserEvent.mealKitId);

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

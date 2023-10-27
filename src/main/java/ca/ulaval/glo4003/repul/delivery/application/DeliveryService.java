package ca.ulaval.glo4003.repul.delivery.application;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
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
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        deliverySystem.addDeliveryPerson(deliveryPersonAccountCreatedEvent.accountId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        deliverySystem.createMealKit(mealKitConfirmedEvent.deliveryLocationId, mealKitConfirmedEvent.mealKitId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent mealKitsCookedEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        KitchenLocationId kitchenLocationId = new KitchenLocationId(mealKitsCookedEvent.kitchenLocationId);

        Cargo cargo = deliverySystem.receiveReadyToBeDeliveredMealKit(kitchenLocationId, mealKitsCookedEvent.mealKitIds);

        deliverySystemRepository.saveOrUpdate(deliverySystem);

        sendReadyToBeDeliverMealKitEvent(cargo, deliverySystem);
    }

    @Subscribe
    public void handleRecallCookedMealKitEvent(RecallCookedMealKitEvent recallCookedMealKitEvent) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        deliverySystem.moveMealKitFromCargosToPending(recallCookedMealKitEvent.mealKitId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);
    }

    private void sendReadyToBeDeliverMealKitEvent(Cargo cargo, DeliverySystem deliverySystem) {
        MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
            new MealKitReceivedForDeliveryEvent(cargo.getCargoId(), cargo.getKitchenLocation().getLocationId(), deliverySystem.getDeliveryPeople(),
                cargo.getMealKits().stream()
                    .map(mealKit -> new MealKitDto(mealKit.getDeliveryLocation().getLocationId(), mealKit.getLockerId(), mealKit.getMealKitId())).toList());
        eventBus.publish(mealKitReceivedForDeliveryEvent);
    }

    public CargosPayload getCargosReadyToPickUp() {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        return CargosPayload.from(deliverySystem.getCargosReadyToPickUp());
    }

    public void pickupCargo(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        List<MealKit> mealKits = deliverySystem.pickupCargo(deliveryPersonId, cargoId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);

        sendPickedUpCargoEvent(mealKits);
    }

    private void sendPickedUpCargoEvent(List<MealKit> mealKits) {
        eventBus.publish(new PickedUpCargoEvent(mealKits.stream().map(MealKit::getMealKitId).toList()));
    }

    public void cancelCargo(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        deliverySystem.cancelCargo(deliveryPersonId, cargoId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);
    }

    public void confirmDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        deliverySystem.confirmDelivery(deliveryPersonId, cargoId, mealKitId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);
    }

    public Optional<LockerId> recallDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        Optional<LockerId> lockerId = deliverySystem.recallDelivery(deliveryPersonId, cargoId, mealKitId);

        deliverySystemRepository.saveOrUpdate(deliverySystem);

        return lockerId;
    }
}

package ca.ulaval.glo4003.repul.shipping.application;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitDeliveryInfoDto;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.shipping.application.exception.ShippingNotFoundException;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final RepULEventBus eventBus;

    public ShippingService(ShippingRepository shippingRepository, RepULEventBus eventBus) {
        this.shippingRepository = shippingRepository;
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handleDeliveryPersonAccountCreatedEvent(DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.addDeliveryPerson(deliveryPersonAccountCreatedEvent.accountId);

        shippingRepository.saveOrUpdate(shipping);
    }

    @Subscribe
    public void receiveShippingRequest(MealKitConfirmedEvent mealKitConfirmedEvent) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.createMealKit(mealKitConfirmedEvent.deliveryLocationId, mealKitConfirmedEvent.mealKitId);

        shippingRepository.saveOrUpdate(shipping);
    }

    @Subscribe
    public void receiveReadyToBeDeliveredMealKit(MealKitsCookedEvent mealKitsCookedEvent) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        KitchenLocationId kitchenLocationId = new KitchenLocationId(mealKitsCookedEvent.pickupLocationId);

        Cargo cargo = shipping.receiveReadyToBeDeliveredMealKit(kitchenLocationId, mealKitsCookedEvent.mealKitIds);

        sendReadyToBeDeliverMealKitEvent(cargo, shipping);

        shippingRepository.saveOrUpdate(shipping);
    }

    private void sendReadyToBeDeliverMealKitEvent(Cargo cargo, Shipping shipping) {
        MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
            new MealKitReceivedForDeliveryEvent(cargo.getCargoId(), cargo.getPickupLocation().getLocationId(),
                shipping.getDeliveryPeople(), cargo.getMealKits().stream().map(
                    mealKit -> new MealKitDeliveryInfoDto(mealKit.getShippingLocation().getLocationId(),
                    mealKit.getLockerId(), mealKit.getMealKitId())).toList());
        eventBus.publish(mealKitReceivedForDeliveryEvent);
    }

    public void pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        List<MealKit> mealKits = shipping.pickupCargo(accountId, shippingId);

        sendPickupCargoEvent(mealKits);

        shippingRepository.saveOrUpdate(shipping);
    }

    private void sendPickupCargoEvent(List<MealKit> mealKits) {
        eventBus.publish(new PickedUpCargoEvent(mealKits.stream().map(MealKit::getMealKitId).toList()));
    }

    public void cancelShipping(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.cancelShipping(accountId, shippingId);

        shippingRepository.saveOrUpdate(shipping);
    }

    public MealKitShippingStatusPayload getShippingStatus(UniqueIdentifier mealKitId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        return MealKitShippingStatusPayload.from(shipping.getMealKit(mealKitId));
    }

    public void confirmShipping(UniqueIdentifier accountId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.confirmShipping(accountId, cargoId, mealKitId);

        shippingRepository.saveOrUpdate(shipping);
    }

    public LockerId unconfirmShipping(UniqueIdentifier accountId, UniqueIdentifier cargoId, UniqueIdentifier mealKitId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        LockerId lockerId = shipping.unconfirmShipping(accountId, cargoId, mealKitId);

        shippingRepository.saveOrUpdate(shipping);

        return lockerId;
    }
}

package ca.ulaval.glo4003.repul.shipping.application;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitDeliveryInfoDto;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.shipping.application.exception.DeliveryAccountNotFoundException;
import ca.ulaval.glo4003.repul.shipping.application.exception.ShippingNotFoundException;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryAccount;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryAccountRepository;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final DeliveryAccountRepository deliveryAccountRepository;
    private final RepULEventBus eventBus;

    public ShippingService(ShippingRepository shippingRepository, DeliveryAccountRepository deliveryAccountRepository, RepULEventBus eventBus) {
        this.shippingRepository = shippingRepository;
        this.deliveryAccountRepository = deliveryAccountRepository;
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handleDeliveryAccountCreatedEvent(DeliveryAccountCreatedEvent deliveryAccountCreatedEvent) {
        deliveryAccountRepository.saveOrUpdate(new DeliveryAccount(deliveryAccountCreatedEvent.accountId));
    }

    @Subscribe
    public void receiveShippingRequest(MealKitConfirmedEvent mealKitConfirmedEvent) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.createMealKitShippingInfo(mealKitConfirmedEvent.deliveryLocationId, mealKitConfirmedEvent.mealKitId);

        shippingRepository.saveOrUpdate(shipping);
    }

    @Subscribe
    public void receiveReadyToBeDeliveredMealKit(MealKitsCookedEvent mealKitsCookedEvent) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        KitchenLocationId kitchenLocationId = new KitchenLocationId(mealKitsCookedEvent.pickupLocationId);

        ShippingTicket shippingTicket = shipping.receiveReadyToBeDeliveredMealKit(kitchenLocationId, mealKitsCookedEvent.mealKitIds);

        sendReadyToBeDeliverMealKitEvent(shippingTicket);

        shippingRepository.saveOrUpdate(shipping);
    }

    private void sendReadyToBeDeliverMealKitEvent(ShippingTicket shippingTicket) {
        MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
            new MealKitReceivedForDeliveryEvent(shippingTicket.getTicketId(), shippingTicket.getPickupLocation().getLocationId(),
                deliveryAccountRepository.getAvailableShippers(), shippingTicket.getMealKitShippingInfos().stream().map(
                    mealKitShippingInfo -> new MealKitDeliveryInfoDto(mealKitShippingInfo.getShippingLocation().getLocationId(),
                    mealKitShippingInfo.getCaseId(), mealKitShippingInfo.getMealKitId())).toList());
        eventBus.publish(mealKitReceivedForDeliveryEvent);
    }

    public void pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        if (!deliveryAccountRepository.exists(accountId)) {
            throw new DeliveryAccountNotFoundException();
        }
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        List<MealKitShippingInfo> mealKitShippingInfoList = shipping.pickupCargo(accountId, shippingId);

        sendPickupCargoEvent(mealKitShippingInfoList);

        shippingRepository.saveOrUpdate(shipping);
    }

    private void sendPickupCargoEvent(List<MealKitShippingInfo> mealKitShippingInfoList) {
        eventBus.publish(new PickedUpCargoEvent(mealKitShippingInfoList.stream().map(MealKitShippingInfo::getMealKitId).toList()));
    }

    public void cancelShipping(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.cancelShipping(accountId, shippingId);

        shippingRepository.saveOrUpdate(shipping);
    }

    public MealKitShippingStatusPayload getShippingStatus(UniqueIdentifier mealKitId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        return MealKitShippingStatusPayload.from(shipping.getMealKitShippingInfo(mealKitId));
    }

    public void confirmShipping(UniqueIdentifier accountId, UniqueIdentifier ticketId, UniqueIdentifier mealKitId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.confirmShipping(accountId, ticketId, mealKitId);

        shippingRepository.saveOrUpdate(shipping);
    }

    public CaseId unconfirmShipping(UniqueIdentifier accountId, UniqueIdentifier ticketId, UniqueIdentifier mealKitId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        CaseId caseId = shipping.unconfirmShipping(accountId, ticketId, mealKitId);

        shippingRepository.saveOrUpdate(shipping);

        return caseId;
    }
}
